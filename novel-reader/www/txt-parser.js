// 文件读取工具 - 使用 FileReader 保证 WebView 兼容性
function readFileAsArrayBuffer(file){
  return new Promise((resolve,reject)=>{
    var fr=new FileReader();
    fr.onload=function(){resolve(fr.result)};
    fr.onerror=function(){reject(fr.error||new Error('读取文件失败'))};
    fr.readAsArrayBuffer(file);
  });
}

// 编码检测
function detectEncoding(buf){
  // BOM 检测
  if(buf.length>=3&&buf[0]===0xEF&&buf[1]===0xBB&&buf[2]===0xBF) return 'utf-8-sig';
  if(buf.length>=2&&buf[0]===0xFF&&buf[1]===0xFE) return 'utf-16le';
  if(buf.length>=2&&buf[0]===0xFE&&buf[1]===0xFF) return 'utf-16be';
  // 启发式检测
  if(isLikelyUTF8(buf)) return 'utf-8';
  // 默认 GBK（中文小说最常见编码）
  return 'gbk';
}

// UTF-8 严格检测：如果字节序列完全符合 UTF-8 规范，则认为是 UTF-8
function isLikelyUTF8(buf){
  var i=0;
  var len=Math.min(buf.length,32768); // 检测前 32KB
  var validMulti=0;
  var invalidCount=0;
  while(i<len){
    var c=buf[i];
    if(c<0x80){
      i++;
      continue;
    }
    if(c>=0xC0&&c<0xE0){
      if(i+1>=len) break;
      if((buf[i+1]&0xC0)!==0x80){invalidCount++;i++;continue}
      // 检查是否是常见中文范围（UTF-8 中文是 3 字节，2字节多为拉丁扩展）
      i+=2;
      validMulti++;
    }else if(c>=0xE0&&c<0xF0){
      if(i+2>=len) break;
      if((buf[i+1]&0xC0)!==0x80||(buf[i+2]&0xC0)!==0x80){invalidCount++;i++;continue}
      i+=3;
      validMulti++;
    }else if(c>=0xF0&&c<0xF8){
      if(i+3>=len) break;
      if((buf[i+1]&0xC0)!==0x80||(buf[i+2]&0xC0)!==0x80||(buf[i+3]&0xC0)!==0x80){invalidCount++;i++;continue}
      i+=4;
      validMulti++;
    }else{
      // 0x80-0xBF 作为首字节是非法 UTF-8，但 GBK 中很常见
      invalidCount++;
      i++;
    }
  }
  // 如果有大量非法 UTF-8 字节，认为是 GBK
  if(invalidCount>5) return false;
  // 如果有多字节序列且没有非法字节，认为是 UTF-8
  if(validMulti>0&&invalidCount===0) return true;
  // 纯 ASCII 也算 UTF-8
  if(validMulti===0&&invalidCount===0) return true;
  return false;
}

// 解码文本 - 使用内置 GBK 解码器，不依赖 TextDecoder
function decodeText(buf,enc){
  if(enc==='utf-16le'){
    return decodeUTF16(buf,true);
  }
  if(enc==='utf-16be'){
    return decodeUTF16(buf,false);
  }
  // UTF-8 和 UTF-8-SIG
  if(enc==='utf-8'||enc==='utf-8-sig'){
    var offset=0;
    if(enc==='utf-8-sig') offset=3;
    return decodeUTF8(buf,offset);
  }
  // GBK - 使用内置解码器
  if(enc==='gbk'){
    return decodeGBK(buf);
  }
  // 兜底
  return decodeGBK(buf);
}

// 内置 UTF-8 解码器（不依赖 TextDecoder）
function decodeUTF8(buf,offset){
  offset=offset||0;
  var result=[];
  var i=offset;
  var len=buf.length;
  while(i<len){
    var c=buf[i];
    if(c<0x80){
      result.push(String.fromCharCode(c));
      i++;
    }else if(c<0xC0){
      // 非法字节，跳过
      result.push('\uFFFD');
      i++;
    }else if(c<0xE0){
      if(i+1<len){
        var cp=((c&0x1F)<<6)|(buf[i+1]&0x3F);
        result.push(String.fromCharCode(cp));
        i+=2;
      }else{result.push('\uFFFD');i++}
    }else if(c<0xF0){
      if(i+2<len){
        var cp=((c&0x0F)<<12)|((buf[i+1]&0x3F)<<6)|(buf[i+2]&0x3F);
        result.push(String.fromCharCode(cp));
        i+=3;
      }else{result.push('\uFFFD');i++}
    }else if(c<0xF8){
      if(i+3<len){
        var cp=((c&0x07)<<18)|((buf[i+1]&0x3F)<<12)|((buf[i+2]&0x3F)<<6)|(buf[i+3]&0x3F);
        // 转成 UTF-16 代理对
        if(cp<=0xFFFF){
          result.push(String.fromCharCode(cp));
        }else{
          cp-=0x10000;
          result.push(String.fromCharCode(0xD800+(cp>>10)));
          result.push(String.fromCharCode(0xDC00+(cp&0x3FF)));
        }
        i+=4;
      }else{result.push('\uFFFD');i++}
    }else{
      result.push('\uFFFD');
      i++;
    }
  }
  return result.join('');
}

// 内置 UTF-16 解码器
function decodeUTF16(buf,littleEndian){
  var result=[];
  var i=0;
  var len=buf.length-(buf.length%2);
  while(i<len){
    var cp;
    if(littleEndian){
      cp=buf[i]|(buf[i+1]<<8);
    }else{
      cp=(buf[i]<<8)|buf[i+1];
    }
    i+=2;
    if(cp>=0xD800&&cp<=0xDBFF&&i+1<len){
      // 高代理，需要读低代理
      var lo;
      if(littleEndian){
        lo=buf[i]|(buf[i+1]<<8);
      }else{
        lo=(buf[i]<<8)|buf[i+1];
      }
      if(lo>=0xDC00&&lo<=0xDFFF){
        cp=0x10000+((cp-0xD800)<<10)+(lo-0xDC00);
        i+=2;
        result.push(String.fromCharCode(0xD800+((cp-0x10000)>>10)));
        result.push(String.fromCharCode(0xDC00+((cp-0x10000)&0x3FF)));
        continue;
      }
    }
    result.push(String.fromCharCode(cp));
  }
  return result.join('');
}

// 章节标题匹配
var CHAPTER_PATTERNS=[
  /^[ \t]{0,4}(第[\s\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]+?(?:章|节|卷|集|部|篇).{0,30})$/,
  /^[ \t]{0,4}((?:序章|楔子|前言|引子|正文|终章|后记|尾声|番外|简介|文案).{0,20})$/,
  /^[ \t]{0,4}((?:Chapter|CHAPTER)\s+\d+.{0,30})$/
];
function isChapterTitle(l){
  var t=l.trim();
  if(!t||t.length>50) return false;
  for(var i=0;i<CHAPTER_PATTERNS.length;i++){
    if(CHAPTER_PATTERNS[i].test(t)) return true;
  }
  return false;
}

function splitChapters(text){
  var lines=text.split(/\r\n|\r|\n/);
  var chs=[];
  var title=null;
  var buf=[];
  for(var i=0;i<lines.length;i++){
    var line=lines[i];
    if(isChapterTitle(line)&&buf.length>0){
      chs.push({title:title||'开始',content:buf.join('\n').trim()});
      title=line.trim();
      buf=[];
    }else{
      if(title===null&&line.trim()) buf.push(line);
      else if(title!==null) buf.push(line);
    }
  }
  if(buf.length>0) chs.push({title:title||'开始',content:buf.join('\n').trim()});
  if(chs.length<=1) return splitBySize(text,5000);
  return chs;
}

function splitBySize(text,sz){
  var chs=[];
  var i=0;
  var n=1;
  while(i<text.length){
    chs.push({title:'第'+n+'节',content:text.slice(i,i+sz).trim()});
    i+=sz;
    n++;
  }
  if(chs.length===0) chs.push({title:'正文',content:text.trim()});
  return chs;
}

function splitParagraphs(c){
  return c.split(/\n+/).map(function(p){return p.trim()}).filter(function(p){return p.length>0});
}

async function parseTXT(file){
  var buf=await readFileAsArrayBuffer(file);
  var u8=new Uint8Array(buf);
  var enc=detectEncoding(u8);
  var text=decodeText(u8,enc);
  // 去除 BOM
  if(text.charCodeAt(0)===0xFEFF) text=text.slice(1);
  var chs=splitChapters(text);
  return{
    title:file.name.replace(/\.txt$/i,''),
    author:'未知',
    format:'txt',
    chapters:chs.map(function(ch,i){return{title:ch.title,paragraphs:splitParagraphs(ch.content),index:i}})
  };
}
