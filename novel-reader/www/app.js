// ===== 状态管理 =====
const state={
  currentBook:null,
  currentChapter:0,
  fontSize:18,
  lineHeight:2.0,
  fontFamily:'sans',
  theme:'default',
  readMode:'scroll',
  longPressPara:null,
  longPressTimer:null,
  searchQuery:'',
  sessionStart:0,
  readTime:0,
  headerVisible:true
};
const $=id=>document.getElementById(id);
const pages={bookshelf:$('page-bookshelf'),reader:$('page-reader')};
let allBooks=[];

// ===== 初始化 =====
async function init(){
  try{
    await openDB();
    loadSettings();
    await loadReadTime();
    await renderBookshelf();
    setupFileInput();
    setupReaderScroll();
    setupLongPress();
    setupClickCenter();
    setupKeyboard();
    // 阅读时长计时
    setInterval(()=>{
      if(pages.reader.classList.contains('active')&&state.currentBook){
        state.readTime+=10;
        saveReadTime();
      }
    },10000);
  }catch(err){
    showToast('初始化失败: '+err.message);
  }
}

// ===== 文件导入 =====
function setupFileInput(){
  $('file-input').addEventListener('change',async(e)=>{
    const f=e.target.files[0];
    if(!f)return;
    e.target.value='';
    await importBookFile(f);
  });
}
function importFile(){$('file-input').click()}

async function importBookFile(file){
  showLoading('正在导入《'+file.name.replace(/\.(txt|epub)$/i,'')+'》...');
  try{
    const ext=file.name.toLowerCase().split('.').pop();
    let r;
    if(ext==='txt') r=await parseTXT(file);
    else if(ext==='epub') r=await parseEPUB(file);
    else if(file.type==='application/epub+zip') r=await parseEPUB(file);
    else r=await parseTXT(file);

    if(!r.chapters||r.chapters.length===0) throw new Error('未解析到内容');
    if(r.chapters.length>2000){
      showToast('内容过大，仅导入前2000章');
      r.chapters=r.chapters.slice(0,2000);
    }

    const bid='book_'+Date.now();
    const book={
      id:bid,
      title:r.title,
      author:r.author,
      format:r.format,
      chapterCount:r.chapters.length,
      importTime:Date.now(),
      coverColor:getCoverColor(bid)
    };
    await dbAddBook(book);
    // 分批写入章节，避免大事务卡顿
    const BATCH=50;
    for(let i=0;i<r.chapters.length;i+=BATCH){
      await dbAddChapters(r.chapters.slice(i,i+BATCH).map((ch,j)=>({
        bookId:bid,index:i+j,title:ch.title,paragraphs:ch.paragraphs
      })));
    }
    hideLoading();
    showToast('导入成功！共'+r.chapters.length+'章');
    await renderBookshelf();
  }catch(err){
    hideLoading();
    showToast('导入失败: '+(err.message||err));
  }
}

// ===== 书架渲染 =====
async function renderBookshelf(){
  allBooks=await dbGetAllBooks();
  allBooks.sort((a,b)=>b.importTime-a.importTime);
  await renderContinueReading();
  renderStats();
  renderBookGrid();
}

function renderBookGrid(){
  const grid=$('bookshelf-grid');
  const empty=$('empty-state');
  const q=state.searchQuery.toLowerCase();
  let books=allBooks;
  if(q) books=allBooks.filter(b=>b.title.toLowerCase().includes(q));

  $('book-count').textContent=books.length+' 本';

  if(allBooks.length===0){
    grid.innerHTML='';
    empty.classList.remove('hidden');
    $('continue-section').style.display='none';
    $('stats-section').style.display='none';
    return;
  }
  empty.classList.add('hidden');

  grid.innerHTML='';
  for(const book of books){
    const card=document.createElement('div');
    card.className='book-card';
    card.innerHTML=
      '<div class="book-cover" style="background:'+book.coverColor+'">'+
        '<span>'+esc(book.title)+'</span>'+
        '<button class="book-delete" onclick="event.stopPropagation();deleteBook(\''+book.id+'\')">'+
          '<svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6 6 18M6 6l12 12"/></svg>'+
        '</button>'+
      '</div>'+
      '<div class="book-name">'+esc(book.title)+'</div>'+
      '<div class="book-meta">'+book.chapterCount+'章 · '+book.format.toUpperCase()+'</div>';
    card.onclick=()=>openBook(book.id);
    grid.appendChild(card);
  }
}

async function renderContinueReading(){
  const sec=$('continue-section');
  if(allBooks.length===0){sec.style.display='none';return}
  // 找最近阅读的书
  let recent=null;
  for(const b of allBooks){
    const p=await dbGetProgress(b.id);
    if(p){b._progress=p;if(!recent||p.updatedAt>recent._progress.updatedAt) recent=b}
  }
  if(!recent){sec.style.display='none';return}
  const ch=await dbGetChapter(recent.id,recent._progress.chapterIndex);
  const percent=Math.round((recent._progress.chapterIndex/Math.max(recent.chapterCount-1,1))*100);
  $('continue-cover').style.background=recent.coverColor;
  $('continue-cover').textContent=recent.title.slice(0,4);
  $('continue-title').textContent=recent.title;
  $('continue-chapter').textContent=ch?('读到: '+ch.title):'';
  $('continue-progress-fill').style.width=percent+'%';
  $('continue-percent').textContent=percent+'%';
  sec.style.display='block';
  sec.dataset.bookId=recent.id;
}

function continueReading(){
  const bid=$('continue-section').dataset.bookId;
  if(bid) openBook(bid);
}

function renderStats(){
  const sec=$('stats-section');
  if(allBooks.length===0){sec.style.display='none';return}
  sec.style.display='block';
  $('stat-books').textContent=allBooks.length;
  let totalCh=0;
  allBooks.forEach(b=>totalCh+=b.chapterCount);
  $('stat-chapters').textContent=totalCh>9999?(totalCh/1000).toFixed(1)+'k':totalCh;
  const h=Math.floor(state.readTime/3600);
  const m=Math.floor((state.readTime%3600)/60);
  $('stat-time').textContent=h>0?h+'h'+m+'m':m+'m';
}

// 搜索
function toggleBookshelfSearch(){
  const wrap=$('search-bar-wrap');
  wrap.classList.toggle('active');
  if(wrap.classList.contains('active')){
    setTimeout(()=>$('book-search-input').focus(),300);
  }else{
    clearBookSearch();
  }
}
function onBookSearch(v){
  state.searchQuery=v;
  $('search-clear').style.display=v?'flex':'none';
  renderBookGrid();
}
function clearBookSearch(){
  state.searchQuery='';
  $('book-search-input').value='';
  $('search-clear').style.display='none';
  renderBookGrid();
}

async function deleteBook(id){
  if(!confirm('确定删除这本书？所有阅读进度和段评将被清除'))return;
  await dbDeleteBook(id);
  await renderBookshelf();
  showToast('已删除');
}

// ===== 阅读器 =====
async function openBook(bid){
  const book=await dbGetBook(bid);
  if(!book)return;
  state.currentBook=book;
  state.sessionStart=Date.now();
  $('reader-title').textContent=book.title;
  $('reader-subtitle').textContent=book.author+' · '+book.format.toUpperCase();
  const p=await dbGetProgress(bid);
  state.currentChapter=p?p.chapterIndex:0;
  pages.bookshelf.classList.remove('active');
  pages.reader.classList.add('active');
  await renderChapter(state.currentChapter);
  if(p&&p.scrollTop) setTimeout(()=>$('reader-main').scrollTop=p.scrollTop,100);
}

function backToBookshelf(){
  pages.reader.classList.remove('active');
  pages.bookshelf.classList.add('active');
  saveCurrentProgress();
  state.currentBook=null;
  renderBookshelf();
}

async function renderChapter(idx){
  if(!state.currentBook)return;
  const ch=await dbGetChapter(state.currentBook.id,idx);
  if(!ch){showToast('章节不存在');return}
  state.currentChapter=idx;
  $('chapter-meta').textContent='第 '+(idx+1)+' 章 / 共 '+state.currentBook.chapterCount+' 章';
  $('chapter-title').textContent=ch.title;
  $('chapter-info-text').textContent='第 '+(idx+1)+' 章';
  const cc=await dbGetCommentCounts(state.currentBook.id,idx);
  const c=$('novel-content');
  c.innerHTML='';
  // 使用 DocumentFragment 提升性能
  const frag=document.createDocumentFragment();
  ch.paragraphs.forEach((text,i)=>{
    const p=document.createElement('div');
    p.className='paragraph';
    p.dataset.paraIndex=i;
    const n=cc[i]||0;
    if(n>0){p.classList.add('has-comments');p.dataset.count=n}
    p.textContent=text;
    frag.appendChild(p);
  });
  c.appendChild(frag);
  updateProgressBar();
  renderTOC();
  $('reader-main').scrollTop=0;
  saveCurrentProgress();
}

async function prevChapter(){
  if(state.currentChapter>0) await renderChapter(state.currentChapter-1);
  else showToast('已经是第一章了');
}
async function nextChapter(){
  if(state.currentChapter<state.currentBook.chapterCount-1) await renderChapter(state.currentChapter+1);
  else showToast('已经是最后一章了');
}

async function renderTOC(){
  const body=$('toc-body');
  body.innerHTML='';
  const frag=document.createDocumentFragment();
  for(let i=0;i<state.currentBook.chapterCount;i++){
    const ch=await dbGetChapter(state.currentBook.id,i);
    if(!ch)continue;
    const item=document.createElement('div');
    item.className='toc-item'+(i===state.currentChapter?' active':'');
    item.innerHTML='<span class="toc-num">'+String(i+1).padStart(2,'0')+'</span>'+
      '<span class="toc-name">'+esc(ch.title)+'</span>'+
      (i<state.currentChapter?'<span class="toc-check">✓</span>':'');
    item.onclick=()=>{renderChapter(i);closeAllPanels()};
    frag.appendChild(item);
  }
  body.appendChild(frag);
}

// ===== 面板控制 =====
function toggleTOC(){
  closeAllPanels();
  setTimeout(()=>{
    $('toc-panel').classList.add('active');
    $('overlay').classList.add('active');
  },10);
}
function toggleSettings(){
  closeAllPanels();
  setTimeout(()=>{
    $('settings-panel').classList.add('active');
    $('overlay').classList.add('active');
  },10);
}
function closeAllPanels(){
  $('toc-panel').classList.remove('active');
  $('settings-panel').classList.remove('active');
  $('overlay').classList.remove('active');
}

// ===== 设置 =====
function setTheme(t){
  state.theme=t;
  document.body.setAttribute('data-theme',t);
  const icon=$('night-icon');
  if(t==='night'){
    icon.innerHTML='<circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>';
    icon.setAttribute('stroke-width','2');
    icon.setAttribute('fill','none');
    icon.setAttribute('stroke','currentColor');
  }else{
    icon.innerHTML='<path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>';
    icon.setAttribute('fill','currentColor');
    icon.removeAttribute('stroke');
  }
  document.querySelectorAll('.theme-option').forEach(e=>e.classList.toggle('active',e.dataset.theme===t));
  saveSettings();
}
function toggleNightMode(){setTheme(state.theme==='night'?'default':'night')}

function setFontSize(v){
  state.fontSize=parseInt(v);
  $('novel-content').style.fontSize=state.fontSize+'px';
  $('font-size-val').textContent=state.fontSize;
  $('font-size-slider').value=state.fontSize;
  saveSettings();
}
function changeFontSize(d){setFontSize(Math.max(14,Math.min(32,state.fontSize+d)))}

function setLineHeight(v){
  state.lineHeight=parseFloat(v);
  $('novel-content').style.lineHeight=state.lineHeight;
  $('line-height-val').textContent=state.lineHeight.toFixed(1);
  $('line-height-slider').value=state.lineHeight;
  saveSettings();
}
function changeLineHeight(d){setLineHeight(Math.max(1.4,Math.min(3.0,parseFloat(state.lineHeight)+d)).toFixed(1))}

function setFontFamily(f){
  state.fontFamily=f;
  const m={
    sans:'"PingFang SC","HarmonyOS Sans SC","Microsoft YaHei",sans-serif',
    serif:'"Noto Serif SC","Songti SC","SimSun",serif',
    kai:'"Kaiti SC","STKaiti","KaiTi",serif'
  };
  $('novel-content').style.fontFamily=m[f]||m.sans;
  document.querySelectorAll('.font-btn[data-font]').forEach(e=>e.classList.toggle('active',e.dataset.font===f));
  saveSettings();
}

function setReadMode(m){
  state.readMode=m;
  document.querySelectorAll('.font-btn[data-mode]').forEach(e=>e.classList.toggle('active',e.dataset.mode===m));
  saveSettings();
}

// ===== 滚动与进度 =====
function setupReaderScroll(){
  let st;
  $('reader-main').addEventListener('scroll',()=>{
    updateProgressBar();
    clearTimeout(st);
    st=setTimeout(saveCurrentProgress,500);
  });
}
function updateProgressBar(){
  const m=$('reader-main');
  const max=m.scrollHeight-m.clientHeight;
  const pct=Math.min(max>0?(m.scrollTop/max)*100:0,100);
  $('progress-fill').style.width=pct+'%';
  $('progress-text').textContent=Math.round(pct)+'%';
}
async function saveCurrentProgress(){
  if(!state.currentBook)return;
  await dbSaveProgress(state.currentBook.id,state.currentChapter,$('reader-main').scrollTop);
}

// ===== 点击区域控制 =====
function setupClickCenter(){
  $('reader-main').addEventListener('click',(e)=>{
    if(e.target.classList.contains('paragraph'))return;
    const m=$('reader-main');
    const r=m.getBoundingClientRect();
    const y=e.clientY-r.top;
    const h=r.height;
    if(state.readMode==='click'){
      if(y<h*0.4) prevChapter();
      else if(y>h*0.6) nextChapter();
      else toggleReaderUI();
    }else{
      if(y<h*0.33) prevChapter();
      else if(y>h*0.66) nextChapter();
      else toggleReaderUI();
    }
  });
}
function toggleReaderUI(){
  const hd=$('reader-header'),ft=$('reader-footer');
  const hidden=!state.headerVisible;
  state.headerVisible=hidden;
  hd.style.transform=hidden?'translateY(-100%)':'';
  ft.style.transform=hidden?'translateY(100%)':'';
  hd.style.transition='transform .25s ease';
  ft.style.transition='transform .25s ease';
}

// ===== 长按段落 =====
function setupLongPress(){
  const c=$('novel-content');
  c.addEventListener('touchstart',(e)=>{
    const p=e.target.closest('.paragraph');
    if(!p)return;
    state.longPressPara=p;
    p.classList.add('long-press');
    state.longPressTimer=setTimeout(()=>{
      showContextMenu(e,parseInt(p.dataset.paraIndex));
    },500);
  });
  c.addEventListener('touchend',()=>{
    clearTimeout(state.longPressTimer);
    if(state.longPressPara) state.longPressPara.classList.remove('long-press');
  });
  c.addEventListener('touchmove',()=>{
    clearTimeout(state.longPressTimer);
    if(state.longPressPara) state.longPressPara.classList.remove('long-press');
  });
  c.addEventListener('click',(e)=>{
    const p=e.target.closest('.paragraph.has-comments');
    if(p) openComments(parseInt(p.dataset.paraIndex));
  });
}

function showContextMenu(e,pi){
  const m=$('context-menu');
  m.dataset.paraIndex=pi;
  const t=e.touches?e.touches[0]:e;
  let x=t.clientX||e.clientX,y=t.clientY||e.clientY;
  if(x+170>window.innerWidth) x=window.innerWidth-180;
  if(y+150>window.innerHeight) y=window.innerHeight-160;
  m.style.left=x+'px';
  m.style.top=y+'px';
  m.classList.add('active');
}

function contextAction(a){
  const pi=parseInt($('context-menu').dataset.paraIndex);
  $('context-menu').classList.remove('active');
  if(a==='comment') openComments(pi,true);
  else if(a==='bookmark') addBookmark(pi);
  else if(a==='copy') copyParagraph(pi);
}

async function copyParagraph(pi){
  const ch=await dbGetChapter(state.currentBook.id,state.currentChapter);
  try{
    await navigator.clipboard.writeText(ch.paragraphs[pi]);
    showToast('已复制');
  }catch{
    // 兼容方案
    const ta=document.createElement('textarea');
    ta.value=ch.paragraphs[pi];
    document.body.appendChild(ta);
    ta.select();
    try{document.execCommand('copy');showToast('已复制')}catch{showToast('复制失败')}
    document.body.removeChild(ta);
  }
}

async function addBookmark(pi){
  const ch=await dbGetChapter(state.currentBook.id,state.currentChapter);
  await dbAddBookmark({bookId:state.currentBook.id,chapterIndex:state.currentChapter,paraIndex:pi,text:ch.paragraphs[pi].slice(0,50),createdAt:Date.now()});
  showToast('书签已添加');
}

// ===== 本章说 =====
let curCP=null;
async function openComments(pi,focus){
  curCP=pi;
  await renderComments(pi);
  $('comment-modal').classList.add('active');
  if(focus) setTimeout(()=>$('comment-input').focus(),300);
}
function toggleComments(){
  curCP=null;
  renderAllChapterComments();
  $('comment-modal').classList.add('active');
}
async function renderComments(pi){
  const body=$('comment-modal-body');
  const cs=await dbGetCommentsByParagraph(state.currentBook.id,state.currentChapter,pi);
  $('comment-count').textContent=cs.length+'条';
  body.innerHTML='';
  if(cs.length===0){
    body.innerHTML='<div style="text-align:center;padding:40px 0;color:var(--text-secondary)"><div style="font-size:40px;margin-bottom:8px;opacity:.4">💬</div><p>暂无段评，来抢沙发~</p></div>';
    return;
  }
  const ch=await dbGetChapter(state.currentBook.id,state.currentChapter);
  const pt=ch.paragraphs[pi];
  if(pt){
    const ref=document.createElement('div');
    ref.className='comment-para-ref';
    ref.textContent=pt.slice(0,80)+(pt.length>80?'...':'');
    body.appendChild(ref);
  }
  cs.sort((a,b)=>b.createdAt-a.createdAt);
  for(const c of cs){
    const d=document.createElement('div');
    d.className='comment-item';
    d.innerHTML='<div class="comment-user"><div class="comment-avatar">'+esc((c.user||'我')[0])+'</div><span class="comment-name">'+esc(c.user||'我')+'</span><span class="comment-time">'+fmtTime(c.createdAt)+'</span></div><div class="comment-text">'+esc(c.content)+'</div>';
    body.appendChild(d);
  }
}
async function renderAllChapterComments(){
  const body=$('comment-modal-body');
  const cs=await dbGetCommentsByChapter(state.currentBook.id,state.currentChapter);
  $('comment-count').textContent=cs.length+'条';
  body.innerHTML='';
  if(cs.length===0){
    body.innerHTML='<div style="text-align:center;padding:40px 0;color:var(--text-secondary)"><div style="font-size:40px;margin-bottom:8px;opacity:.4">💬</div><p>本章还没有评论</p><p style="font-size:13px;margin-top:4px">长按段落可以添加段评</p></div>';
    return;
  }
  const ch=await dbGetChapter(state.currentBook.id,state.currentChapter);
  cs.sort((a,b)=>b.createdAt-a.createdAt);
  for(const c of cs){
    const pt=ch.paragraphs[c.paraIndex];
    const d=document.createElement('div');
    d.className='comment-item';
    d.innerHTML=(pt?'<div class="comment-para-ref">'+esc(pt.slice(0,60))+'...</div>':'')+
      '<div class="comment-user"><div class="comment-avatar">'+esc((c.user||'我')[0])+'</div><span class="comment-name">'+esc(c.user||'我')+'</span><span class="comment-time">'+fmtTime(c.createdAt)+'</span></div><div class="comment-text">'+esc(c.content)+'</div>';
    body.appendChild(d);
  }
}
function closeCommentModal(){$('comment-modal').classList.remove('active')}
async function submitComment(){
  const inp=$('comment-input');
  const t=inp.value.trim();
  if(!t){showToast('请输入评论内容');return}
  if(curCP===null){showToast('请长按段落添加段评');return}
  await dbAddComment({bookId:state.currentBook.id,chapterIndex:state.currentChapter,paraIndex:curCP,content:t,user:'我',createdAt:Date.now()});
  inp.value='';
  showToast('评论发表成功');
  await renderComments(curCP);
  await refreshParaComments();
}
async function refreshParaComments(){
  const cc=await dbGetCommentCounts(state.currentBook.id,state.currentChapter);
  document.querySelectorAll('.paragraph').forEach(p=>{
    const i=parseInt(p.dataset.paraIndex);
    const n=cc[i]||0;
    if(n>0){p.classList.add('has-comments');p.dataset.count=n}
    else{p.classList.remove('has-comments');delete p.dataset.count}
  });
}

// ===== 键盘支持（部分设备） =====
function setupKeyboard(){
  document.addEventListener('keydown',(e)=>{
    if(!pages.reader.classList.contains('active'))return;
    if(e.key==='ArrowLeft') prevChapter();
    else if(e.key==='ArrowRight') nextChapter();
  });
}

// ===== 设置持久化 =====
function saveSettings(){
  localStorage.setItem('reader-settings',JSON.stringify({
    fontSize:state.fontSize,
    lineHeight:state.lineHeight,
    fontFamily:state.fontFamily,
    theme:state.theme,
    readMode:state.readMode
  }));
}
function loadSettings(){
  try{
    const s=JSON.parse(localStorage.getItem('reader-settings')||'{}');
    if(s.fontSize) state.fontSize=s.fontSize;
    if(s.lineHeight) state.lineHeight=s.lineHeight;
    if(s.fontFamily) state.fontFamily=s.fontFamily;
    if(s.theme) state.theme=s.theme;
    if(s.readMode) state.readMode=s.readMode;
  }catch{}
  document.body.setAttribute('data-theme',state.theme);
  setTheme(state.theme);
  setFontSize(state.fontSize);
  setLineHeight(state.lineHeight);
  setFontFamily(state.fontFamily);
  document.querySelectorAll('.font-btn[data-mode]').forEach(e=>e.classList.toggle('active',e.dataset.mode===state.readMode));
}

function saveReadTime(){
  localStorage.setItem('reader-readtime',String(state.readTime));
}
function loadReadTime(){
  state.readTime=parseInt(localStorage.getItem('reader-readtime')||'0');
}

// ===== 工具函数 =====
function showToast(msg){
  const t=$('toast');
  t.textContent=msg;
  t.classList.add('show');
  clearTimeout(t._t);
  t._t=setTimeout(()=>t.classList.remove('show'),2200);
}
function showLoading(t){$('loading-text').textContent=t||'加载中...';$('loading-overlay').classList.add('active')}
function hideLoading(){$('loading-overlay').classList.remove('active')}
function esc(s){if(!s)return'';return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;')}
function fmtTime(ts){
  const d=Date.now()-ts;
  if(d<60000) return'刚刚';
  if(d<3600000) return Math.floor(d/60000)+'分钟前';
  if(d<86400000) return Math.floor(d/3600000)+'小时前';
  return Math.floor(d/86400000)+'天前';
}
function getCoverColor(id){
  const cs=[
    'linear-gradient(135deg,#e67e22,#c0392b)',
    'linear-gradient(135deg,#4a8fc4,#2a5f8a)',
    'linear-gradient(135deg,#4a9c6a,#2a6a4a)',
    'linear-gradient(135deg,#8a6ac4,#5a3a8a)',
    'linear-gradient(135deg,#d4a44a,#8a6a2a)',
    'linear-gradient(135deg,#4a9c9c,#2a6a6a)',
    'linear-gradient(135deg,#c44a4a,#8a2a2a)',
    'linear-gradient(135deg,#6a8ac4,#3a5a8a)',
    'linear-gradient(135deg,#b04ac4,#7a2a8a)',
    'linear-gradient(135deg,#4ac4a4,#2a8a6a)'
  ];
  let h=0;
  for(let i=0;i<id.length;i++){h=((h<<5)-h)+id.charCodeAt(i);h|=0}
  return cs[Math.abs(h)%cs.length];
}

// ===== 事件绑定 =====
$('overlay').addEventListener('click',closeAllPanels);
$('comment-modal').addEventListener('click',(e)=>{if(e.target===$('comment-modal'))closeCommentModal()});
document.addEventListener('click',(e)=>{
  const m=$('context-menu');
  if(m.classList.contains('active')&&!m.contains(e.target)) m.classList.remove('active');
});
// 阻止双击缩放
let lastTouch=0;
document.addEventListener('touchend',(e)=>{
  const now=Date.now();
  if(now-lastTouch<300){e.preventDefault()}
  lastTouch=now;
},{passive:false});

document.addEventListener('DOMContentLoaded',init);
