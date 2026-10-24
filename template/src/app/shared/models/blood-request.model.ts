export interface BloodRequest {
  id: number;
  hospitalName: string;
  bloodType: string;
  quantity: number;
  urgency: 'NORMAL' | 'URGENT' | 'CRITICAL';
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'DELIVERED';
  date: string;
}
