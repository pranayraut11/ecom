const jsonServer = require('json-server');
const server = jsonServer.create();
const router = jsonServer.router('json-server-db.json');
const middlewares = jsonServer.defaults();

// Add CORS headers
server.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', '*');
  next();
});

server.use(middlewares);
server.use(jsonServer.bodyParser);

// Custom request handling
server.use((req, res, next) => {
  // Custom login response
  if (req.method === 'POST' && req.url === '/user-service/auth/login') {
    req.body = {
      "access_token": "asdsadasd",
      "refresh_token": "asdsadasd",
      "expires_in": "123123123123",
      "roles": [req.body.username]
    };
  }


  
  next();
});

// Add routing url 
server.use(jsonServer.rewriter({
  '/product-service/products*': '/products$1',
  '/product-service/search/:id': '/data/?category_like=:id',
  '/product-service/searchbyname/:id': '/data/?name_like=:id',
  '/cart-service/cart': '/cart',
  '/cart-service/cart/:id': '/cart/:id',
  '/user-service/auth/login': '/login',
  '/user-service/auth/logout':'/login',
  '/user-service/address': '/address',
  '/user-service/address/:id': '/address/:id',
  '/category-service/': '/categories',
  '/menus': '/menus',
  '/product-service/search/filtered/:id': '/data/?name_like=:id'

}));

server.use(router);

// Handle product filtering and pagination
server.use((req, res, next) => {
  if (req.method === 'GET' && req.path === '/products') {
    // Get query parameters
    const page = parseInt(req.query.page) || 0;
    const size = parseInt(req.query.size) || 10;
    const category = req.query.category;
    
    const db = router.db.getState();
    const products = db.products.data;
    
    // Filter by category if provided
    const filteredProducts = category 
      ? products.filter(p => p.category === category)
      : products;
    
    // Apply pagination
    const start = page * size;
    const end = start + size;
    const paginatedProducts = filteredProducts.slice(start, end);
    
    // Create response with pagination info
    const response = {
      totalElements: filteredProducts.length,
      totalPages: Math.ceil(filteredProducts.length / size),
      first: page === 0,
      last: end >= filteredProducts.length,
      size: size,
      number: page,
      data: paginatedProducts
    };
    
    res.jsonp(response);
    return;
  }

  
  next();
});

// Start server
const port = 3000;
server.listen(port, () => {
  console.log(`JSON Server is running on port ${port}`);
});
