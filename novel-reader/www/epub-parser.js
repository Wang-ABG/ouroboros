async function parseEPUB(file){
  const ab=await readFileAsArrayBuffer(file);
  const zip=await JSZip.loadAsync(ab);
  const cxml=await zip.file('META-INF/container.xml').async('text');
  const cdoc=new DOMParser().parseFromString(cxml,'application/xml');
  const opfPath=cdoc.querySelector('rootfile').getAttribute('full-path');
  const oxml=await zip.file(opfPath).async('text');
  const odoc=new DOMParser().parseFromString(oxml,'application/xml');
  const tEl=odoc.querySelector('metadata > title, dc\\:title');
  const cEl=odoc.querySelector('metadata > creator, dc\\:creator');
  const bookTitle=tEl?tEl.textContent.trim():file.name.replace(/\.epub$/i,'');
  const author=cEl?cEl.textContent.trim():'未知';
  const manifest={};
  odoc.querySelectorAll('manifest > item').forEach(it=>{
    manifest[it.getAttribute('id')]={href:it.getAttribute('href'),mediaType:it.getAttribute('media-type')};
  });
  const spine=[];
  odoc.querySelectorAll('spine > itemref').forEach(it=>{
    if(manifest[it.getAttribute('idref')]) spine.push(it.getAttribute('idref'));
  });
  const opfDir=opfPath.includes('/')?opfPath.substring(0,opfPath.lastIndexOf('/')+1):'';
  const chapters=[];
  for(const id of spine){
    const item=manifest[id];
    if(!item||(!item.mediaType.includes('html')&&!item.mediaType.includes('xml'))) continue;
    const fp=resolvePath(opfDir,item.href);
    const f=zip.file(fp);
    if(!f) continue;
    const html=await f.async('text');
    const doc=new DOMParser().parseFromString(html,'text/html');
    let ct='';
    const h=doc.querySelector('h1,h2,h3,title');
    if(h) ct=h.textContent.trim();
    const paras=[];
    doc.querySelectorAll('p').forEach(el=>{
      const t=el.textContent.trim();
      if(t.length>0) paras.push(t);
    });
    if(paras.length===0){
      const bt=(doc.body||doc.documentElement).textContent.trim();
      bt.split(/\n+/).forEach(l=>{const t=l.trim();if(t)paras.push(t)});
    }
    const up=[];const seen=new Set();
    for(const p of paras){
      if(!seen.has(p)&&p.length>1){seen.add(p);up.push(p)}
    }
    if(up.length>0) chapters.push({title:ct||`第${chapters.length+1}章`,paragraphs:up,index:chapters.length});
  }
  if(chapters.length===0) chapters.push({title:'正文',paragraphs:['无法解析EPUB内容'],index:0});
  return{title:bookTitle,author:author,format:'epub',chapters};
}
function resolvePath(base,rel){
  if(!base) return rel;
  const ps=rel.split('/');
  const st=base.split('/').filter(p=>p&&p!=='.');
  for(const p of ps){
    if(p==='..') st.pop();
    else if(p!=='.'&&p) st.push(p);
  }
  return st.join('/');
}
