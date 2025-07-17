const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('json-server-db.json')

const middlewares = jsonServer.defaults()

// Add CORS headers
server.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Headers', '*')
  next()
})


server.use(middlewares)


server.use(jsonServer.bodyParser)
// Custom login response 
server.use((req, res, next) => {
  if (req.method === 'POST' & req.url === '/user-service/auth/login') {
    req.body = {
      "access_token": "asdsadasd",
      "refresh_token": "asdsadasd",
      "expires_in": "123123123123",
      "roles": [req.body.username]
  }
  }
  if (req.method === 'POST' & req.url === '/cart-service/cart/product-ids') {
    req.method = 'GET';
    req.url = "/cart-service/cart";
  }
  
  // Handle product detail requests
  if (req.method === 'GET' && (req.url.startsWith('/product-service/product/') || req.url.startsWith('/catalog/product/'))) {
    const productId = req.url.split('/').pop();
    const db = router.db.getState();
    const product = db.products.data.find(p => p.id === productId);
    
    if (product) {
      res.jsonp(product);
      return;
    }
  }
  
  // Continue to JSON Server router
  next()
})

//Add routing url 
server.use(jsonServer.rewriter({
  '/product-service/products*': '/products$1', // Handle all product routes with query params
  '/product-service/product/:id': '/products/data/:id',
  '/cart-service/cart': '/cart',
  '/cart-service/cart/:id': '/cart/:id',
  '/user-service/auth/login': '/login',
  '/user-service/address' : '/address',
  '/user-service/address/:id' : '/address/:id',
  '/category-service/' : '/categories',
  '/menus': '/menus',
  '/product/:id': '/products/data/:id'
}))

server.use(router)

// Handle product filtering and pagination
server.use((req, res, next) => {
  if (req.method === 'GET' && req.path === '/products') {
    // Get query parameters
    const page = parseInt(req.query.page) || 0;
    const size = parseInt(req.query.size) || 10;
    const category = req.query.category;
    const maxPrice = parseFloat(req.query.maxPrice);
    const minPrice = parseFloat(req.query.minPrice);

    // Get the base data
    const db = router.db;
    let data = db.get('products.data').value() || [];

    // Apply filters
    if (category) {
      data = data.filter(product => product.category === category);
    }
    if (maxPrice) {
      data = data.filter(product => parseFloat(product.price.price) <= maxPrice);
    }
    if (minPrice) {
      data = data.filter(product => parseFloat(product.price.price) >= minPrice);
    }

    // Calculate pagination
    const totalElements = data.length;
    const totalPages = Math.ceil(totalElements / size);
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const paginatedData = data.slice(startIndex, endIndex);

    // Construct response
    const response = {
      totalElements,
      totalPages,
      first: page === 0,
      last: page >= totalPages - 1,
      size,
      number: page,
      data: paginatedData
    };

    res.json(response);
    return;
  }
  next();
})

server.listen(3000, () => {
  console.log('JSON Server is running on PORT 3000 ')
})