export type UserRole = 'DONOR' | 'HOSPITAL' | 'CENTER' | 'ADMIN';

export interface User {
  id?: number;            // id en base (disponible via GET /api/me)
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
}
