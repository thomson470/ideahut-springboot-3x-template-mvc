import{_ as S,aH as V,aO as k,Z as f,r as g,a0 as v,$ as T,a4 as q,a5 as u,ab as _,f as d,a3 as c,aK as C,a7 as Q,a8 as j,C as A,a2 as b,a6 as y,F as h,ac as x,aL as E,aa as F,aM as L,aJ as P,aI as I}from"./index.b49d2b73.js";import{Q as U,b as D,a as B}from"./format.ec7b688e.js";import{Q as N}from"./QTooltip.6433a2f9.js";import{C as O}from"./ClosePopup.102950a7.js";import{g as a}from"./Index.13c26c29.js";import"./QSpace.ae60b3df.js";import"./QTable.5a77fb0c.js";import"./QInnerLoading.99779149.js";import"./QSpinnerGears.a9b39caa.js";const R={props:["parameters"],components:{FormV:V(()=>k(()=>import("./FormV.18422905.js"),["assets/FormV.18422905.js","assets/format.ec7b688e.js","assets/index.b49d2b73.js","assets/index.048c1e36.css","assets/QTooltip.6433a2f9.js","assets/ClosePopup.102950a7.js","assets/Index.13c26c29.js","assets/QSpace.ae60b3df.js","assets/QTable.5a77fb0c.js","assets/QInnerLoading.99779149.js","assets/QSpinnerGears.a9b39caa.js"])),Table2:V(()=>k(()=>import("./Table2.b2a1637c.js"),["assets/Table2.b2a1637c.js","assets/index.b49d2b73.js","assets/index.048c1e36.css","assets/format.ec7b688e.js","assets/QTooltip.6433a2f9.js","assets/QTable.5a77fb0c.js","assets/QSpace.ae60b3df.js","assets/QInnerLoading.99779149.js","assets/Index.13c26c29.js","assets/QSpinnerGears.a9b39caa.js"]))},setup(){return{util:f,title:g(null),fields:g([]),forms:g([]),tables:g([]),loading:g({}),replica:g(null),dialog:g({form:{show:!1,parameters:null},table:{show:!1,parameters:null}})}},created(){let l=this,o=a.get.object(l.parameters),r=a.get.object(o.row),e=a.get.object(o.template);l.title=f.isString(o.title)?o.title:e.title,l.replica=a.get.number(o.replica,null),l.fields=[];let p=a.get.array(e.table.columns),i,m=!1;for(const n of p)!m&&e.id.fields.includes(n.field)&&(m=!0),i=f.getFieldValue(n.field,r),f.isFunction(n.format)&&(i=n.format(i,r)),l.fields.push({label:n.label,value:i});if(!m){let n=a.id.toPk(e.id,r);f.isDefined(n)&&n!==null&&l.fields.splice(0,0,{label:"ID",value:n})}l.forms=a.get.array(e.forms),l.tables=a.get.array(e.children)},methods:{on_form_click(l,o){let r=this,e=a.get.object(r.parameters),p=a.get.object(e.template),i=a.get.object(e.row),m=a.get.array(l.relations);if(!m.length){v.error("error.required","label.relation");return}let n=[];for(const s of m)n.push({field:s.target,condition:"EQUAL",value:f.getFieldValue(s.source,i)});let t=a.copy(l.crud);t.filters=f.isArray(t.filters)?t.filters:[],t.filters=t.filters.concat(n),f.isNumber(r.replica)&&(t.replica=r.replica),r.loading["form_"+o]=!0,T.call({path:"/crud/single",method:"post",data:t,onFinish(){r.loading["form_"+o]=!1},onSuccess(s){s=a.get.object(s),a.inject.pkAndGridId(l.id,s,p._grid_id_),r.dialog.form={show:!0,parameters:{form:l,data:s,replica:r.replica}}}})},on_table_click(l){let o=this,r=a.get.object(o.parameters),e=a.get.object(r.template),p=a.get.object(r.row),i=a.copy(a.get.array(l.relations));if(!i.length){v.error("error.required","label.relation");return}for(const m of i)m.value=f.getFieldValue(m.source,p);l._grid_id_=e._grid_id_,o.dialog.table={show:!0,parameters:{template:e,definition:l,parentRow:p,relations:i,onlyView:!0,replica:o.replica}}},on_close_dialog_table(l){let o=this;o.dialog.table={show:!1,parameters:null}}}},z={key:0,class:"q-mb-xs",style:{width:"100%"}},G={key:1,class:"q-mb-xs",style:{width:"100%"}};function H(l,o,r,e,p,i){const m=q("FormV"),n=q("Table2");return u(),_(h,null,[d(P,{style:L("width: "+(l.$q.screen.lt.md?"100%;":"50%;"))},{default:c(()=>[d(C,{class:"header-main"},{default:c(()=>[d(U,{class:"q-pr-none"},{default:c(()=>[d(D,null,{default:c(()=>[d(B,{class:"text-h6 text-white"},{default:c(()=>[Q(j(e.title),1)]),_:1})]),_:1}),d(D,{side:""},{default:c(()=>[A((u(),b(y,{class:"text-caption text-white q-pl-xs q-pr-xs q-mr-xs",flat:"",round:"",glossy:"",icon:"close"},{default:c(()=>[d(N,null,{default:c(()=>[Q(j(l.$t("label.close")),1)]),_:1})]),_:1})),[[O]])]),_:1})]),_:1})]),_:1}),d(C,{style:{"max-height":"75vh"},class:"q-pa-xs q-mt-none scroll"},{default:c(()=>[(u(!0),_(h,null,x(e.fields,(t,s)=>(u(),b(E,{type:"text",key:s,label:t.label,modelValue:t.value,"onUpdate:modelValue":w=>t.value=w,readonly:"",filled:"",autogrow:"",class:"q-mb-xs",style:{"max-height":"200px",overflow:"scroll"}},null,8,["label","modelValue","onUpdate:modelValue"]))),128)),e.forms.length?(u(),_("div",z,[(u(!0),_(h,null,x(e.forms,(t,s)=>(u(),b(y,{key:s,label:t.title,class:"full-width q-mt-xs q-mb-xs text-weight-bold","no-caps":"",glossy:"",loading:e.loading["form_"+s],onClick:w=>i.on_form_click(t,s)},null,8,["label","loading","onClick"]))),128))])):F("",!0),e.tables.length?(u(),_("div",G,[(u(!0),_(h,null,x(e.tables,(t,s)=>(u(),b(y,{key:s,label:t.title,class:"full-width q-mt-xs q-mb-xs text-weight-bold","no-caps":"",glossy:"",onClick:w=>i.on_table_click(t)},null,8,["label","onClick"]))),128))])):F("",!0)]),_:1})]),_:1},8,["style"]),d(I,{modelValue:e.dialog.form.show,"onUpdate:modelValue":o[0]||(o[0]=t=>e.dialog.form.show=t),persistent:"","transition-show":"slide-down","transition-hide":"none","backdrop-filter":"blur(2px)"},{default:c(()=>[d(m,{parameters:e.dialog.form.parameters},null,8,["parameters"])]),_:1},8,["modelValue"]),d(I,{modelValue:e.dialog.table.show,"onUpdate:modelValue":o[1]||(o[1]=t=>e.dialog.table.show=t),"transition-show":"slide-down","transition-hide":"none","backdrop-filter":"blur(2px)","full-height":""},{default:c(()=>[d(n,{parameters:e.dialog.table.parameters,onClose:i.on_close_dialog_table},null,8,["parameters","onClose"])]),_:1},8,["modelValue"])],64)}var le=S(R,[["render",H]]);export{le as default};