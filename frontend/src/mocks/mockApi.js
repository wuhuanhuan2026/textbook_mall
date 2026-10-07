// frontend/src/mocks/mockApi.js
const wait = ms => new Promise(resolve => setTimeout(resolve, ms))

export async function mockRequest(work, options = {}) {
  await wait(options.delay ?? 500)
  if (options.fail) {
    const error = new Error(options.message ?? '模拟网络失败')
    error.code = 'MOCK_ERROR'
    throw error
  }
  return structuredClone(work())
}

export function paginate(records, pageNum = 1, pageSize = 10) {
  const start = (pageNum - 1) * pageSize
  return {
    records: records.slice(start, start + pageSize),
    total: records.length,
    pageNum,
    pageSize
  }
}
