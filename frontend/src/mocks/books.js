// frontend/src/mocks/books.js
export const categories = [
  { categoryId: 1, categoryName: '计算机' },
  { categoryId: 2, categoryName: '数学' },
  { categoryId: 3, categoryName: '外语' },
  { categoryId: 4, categoryName: '文学' }
]

function createInitialBooks() {
  return [
    { bookId: 1, bookName: 'Java核心技术 卷I', author: 'Cay S. Horstmann', publisher: '机械工业出版社', isbn: '9787111636666', categoryId: 1, categoryName: '计算机', price: 99.00, originalPrice: 149.00, stock: 50, sales: 120, coverImage: '', description: 'Java领域经典著作。', status: 'PUBLISHED' },
    { bookId: 2, bookName: 'Vue.js设计与实现', author: '霍春阳', publisher: '人民邮电出版社', isbn: '9787115583867', categoryId: 1, categoryName: '计算机', price: 89.00, originalPrice: 119.00, stock: 30, sales: 200, coverImage: '', description: '深入剖析Vue.js 3。', status: 'PUBLISHED' },
    { bookId: 3, bookName: '深入理解计算机系统', author: 'Randal E. Bryant', publisher: '机械工业出版社', isbn: '9787111544937', categoryId: 1, categoryName: '计算机', price: 139.00, originalPrice: 239.00, stock: 0, sales: 80, coverImage: '', description: '计算机科学经典教材。', status: 'PUBLISHED' },
    { bookId: 4, bookName: '高等数学 上册', author: '同济大学数学系', publisher: '高等教育出版社', isbn: '9787040396638', categoryId: 2, categoryName: '数学', price: 45.00, originalPrice: 59.00, stock: 100, sales: 500, coverImage: '', description: '国内高校广泛使用。', status: 'PUBLISHED' },
    { bookId: 5, bookName: '新概念英语 第二册', author: 'L.G. Alexander', publisher: '外语教学与研究出版社', isbn: '9787560026544', categoryId: 3, categoryName: '外语', price: 38.00, originalPrice: 48.00, stock: 80, sales: 300, coverImage: '', description: '经典英语学习教材。', status: 'PUBLISHED' },
    { bookId: 6, bookName: '百年孤独', author: '加西亚马尔克斯', publisher: '南海出版公司', isbn: '9787544253994', categoryId: 4, categoryName: '文学', price: 39.50, originalPrice: 55.00, stock: 60, sales: 150, coverImage: '', description: '魔幻现实主义代表作。', status: 'PUBLISHED' },
    { bookId: 7, bookName: '算法导论', author: 'Thomas H. Cormen', publisher: '机械工业出版社', isbn: '9787111407010', categoryId: 1, categoryName: '计算机', price: 128.00, originalPrice: 158.00, stock: 25, sales: 60, coverImage: '', description: '算法领域经典教材。', status: 'OFF_SHELF' },
    { bookId: 8, bookName: '线性代数', author: '同济大学数学系', publisher: '高等教育出版社', isbn: '9787040396614', categoryId: 2, categoryName: '数学', price: 35.00, originalPrice: 45.00, stock: 90, sales: 420, coverImage: '', description: '工科线性代数经典。', status: 'PUBLISHED' }
  ]
}

export let books = createInitialBooks()

export function resetBooks() {
  books.length = 0
  books.push(...createInitialBooks())
}
