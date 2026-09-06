export const PRIVILEGED_ROLES: number[] = [10, 100]

export const isPrivilegedRole = (role?: number | null): boolean =>
  role !== undefined && role !== null && PRIVILEGED_ROLES.includes(role)
