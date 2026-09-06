import request from './index'
import type { QueryRequest } from './analyzer'

export type ExportRequest = QueryRequest

export const exportData = (params: ExportRequest) => {
  return request.post<Blob>('/export/data', params, {
    responseType: 'blob'
  })
}
