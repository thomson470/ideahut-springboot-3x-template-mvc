import{l as b,v,ao as f,y,ar as S,c as t,h as n,p as m,g as C,bp as h}from"./index.b49d2b73.js";var k=b({name:"QInnerLoading",props:{...v,...f,showing:Boolean,color:String,size:{type:[String,Number],default:"42px"},label:String,labelClass:String,labelStyle:[String,Array,Object]},setup(e,{slots:a}){const s=C(),r=y(e,s.proxy.$q),{transitionProps:i,transitionStyle:o}=S(e),u=t(()=>"q-inner-loading q--avoid-card-border absolute-full column flex-center"+(r.value===!0?" q-inner-loading--dark":"")),c=t(()=>"q-inner-loading__label"+(e.labelClass!==void 0?` ${e.labelClass}`:""));function d(){const l=[n(h,{size:e.size,color:e.color})];return e.label!==void 0&&l.push(n("div",{class:c.value,style:e.labelStyle},[e.label])),l}function g(){return e.showing===!0?n("div",{class:u.value,style:o.value},a.default!==void 0?a.default():d()):null}return()=>n(m,i.value,g)}});export{k as Q};