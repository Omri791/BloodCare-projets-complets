export interface Donation { id:number; donorName:string; bloodType:string; centerName:string; date:string; status:'PENDING'|'ACCEPTED'|'COMPLETED'|'CANCELLED'; quantity:number; }
