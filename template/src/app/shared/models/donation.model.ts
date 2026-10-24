export type DonationStatus = 'PENDING' | 'ACCEPTED' | 'COMPLETED' | 'CANCELLED';

export interface Donation {
  id: number;
  donorName: string;
  bloodType: string;
  centerName: string;
  date: string;
  status: DonationStatus;
  quantity: number;
}
