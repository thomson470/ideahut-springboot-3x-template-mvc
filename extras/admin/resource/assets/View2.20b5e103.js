import{Q as w,b as x,a as y}from"./format.650a3fa7.js";import{Q as v}from"./QTooltip.3cd33127.js";import{_ as Q,r as u,Y as m,a4 as f,a1 as c,a2 as l,f as s,aI as b,a6 as h,a7 as _,C as q,a5 as V,aa as I,F as C,ab as k,aJ as S,aK as B,aH as D}from"./index.cf0e6694.js";import{C as F}from"./ClosePopup.fb083e6c.js";import{g as r}from"./Index.896d75d9.js";import"./QSpace.1021ecfc.js";import"./QTable.4293fe89.js";import"./QBadge.062f3943.js";import"./QInnerLoading.1086a0a2.js";import"./QSpinnerGears.0625f03b.js";const $={props:["parameters"],setup(){return{title:u(null),fields:u([]),forms:u({}),loading:u({})}},created(){let t=this,i=r.get.object(t.parameters),d=r.get.object(i.row),o=r.get.object(i.definition);t.title=i.title,t.replica=r.get.number(i.replica,null),t.fields=[];let p=r.get.array(o.table.columns),n,a=!1;for(const e of p)!a&&o.id.fields.includes(e.field)&&(a=!0),n=m.getFieldValue(e.field,d),m.isFunction(e.format)&&(n=e.format(n,d)),t.fields.push({label:e.label,value:n});if(!a){let e=r.id.toPk(o.id,d);m.isDefined(e)&&e!==null&&t.fields.splice(0,0,{label:"ID",value:e})}}};function j(t,i,d,o,p,n){return f(),c(D,{style:B("width: "+(t.$q.screen.lt.md?"100%;":"50%;"))},{default:l(()=>[s(b,{class:"q-pa-none header-main"},{default:l(()=>[s(w,{class:"q-pr-none"},{default:l(()=>[s(x,null,{default:l(()=>[s(y,{class:"text-h6 text-white"},{default:l(()=>[h(_(o.title),1)]),_:1})]),_:1}),s(x,{side:""},{default:l(()=>[q((f(),c(V,{class:"text-caption text-white q-pl-xs q-pr-xs q-mr-xs",flat:"",round:"",glossy:"",icon:"close"},{default:l(()=>[s(v,null,{default:l(()=>[h(_(t.$t("label.close")),1)]),_:1})]),_:1})),[[F]])]),_:1})]),_:1})]),_:1}),s(b,{style:{"max-height":"75vh"},class:"q-pa-xs q-mt-xs scroll"},{default:l(()=>[(f(!0),I(C,null,k(o.fields,(a,e)=>(f(),c(S,{type:"text",key:e,label:a.label,modelValue:a.value,"onUpdate:modelValue":g=>a.value=g,readonly:"",filled:"",autogrow:"",class:"q-mb-xs",style:{"max-height":"200px",overflow:"scroll"}},null,8,["label","modelValue","onUpdate:modelValue"]))),128))]),_:1})]),_:1},8,["style"])}var Y=Q($,[["render",j]]);export{Y as default};