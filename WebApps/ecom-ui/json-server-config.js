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
  
  // Handle cart requests
  if (req.method === 'GET' && req.url === '/cart-service/cart/product-ids') {
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
  
  // Handle address requests
  if (req.url.startsWith('/user-service/address')) {
    const db = router.db.getState();
    
    // Get all addresses
    if (req.method === 'GET' && req.url === '/user-service/address') {
      res.jsonp(db.addresses || []);
      return;
    }
    
    // Get address by ID
    if (req.method === 'GET' && req.url.match(/\/user-service\/address\/[a-zA-Z0-9-]+$/)) {
      const addressId = req.url.split('/').pop();
      const address = db.addresses.find(a => a.id === addressId);
      
      if (address) {
        res.jsonp(address);
      } else {
        res.status(404).jsonp({ error: "Address not found" });
      }
      return;
    }
    
    // Add or update address
    if (req.method === 'POST' && req.url === '/user-service/address') {
      const newAddress = req.body;
      const addresses = db.addresses || [];
      
      if (newAddress.id) {
        // Update existing address
        const index = addresses.findIndex(a => a.id === newAddress.id);
        if (index !== -1) {
          addresses[index] = newAddress;
          db.addresses = addresses;
          router.db.setState(db);
          res.jsonp(newAddress);
        } else {
          res.status(404).jsonp({ error: "Address not found" });
        }
      } else {
        // Add new address
        newAddress.id = 'addr-' + (Math.floor(Math.random() * 9000) + 1000);
        addresses.push(newAddress);
        db.addresses = addresses;
        router.db.setState(db);
        res.jsonp(newAddress);
      }
      return;
    }
    
    // Delete address
    if (req.method === 'DELETE' && req.url.match(/\/user-service\/address\/[a-zA-Z0-9-]+$/)) {
      const addressId = req.url.split('/').pop();
      const addresses = db.addresses || [];
      const index = addresses.findIndex(a => a.id === addressId);
      
      if (index !== -1) {
        addresses.splice(index, 1);
        db.addresses = addresses;
        router.db.setState(db);
        res.jsonp({ success: true });
      } else {
        res.status(404).jsonp({ error: "Address not found" });
      }
      return;
    }
  }
  
  // Handle menu requests
  if (req.method === 'GET' && req.url === '/menus') {
    const db = router.db.getState();
    const menus = db.menus;
    
    if (menus) {
      res.jsonp(menus);
      return;
    }
  }
  
  next();
});

// Add routing url 
server.use(jsonServer.rewriter({
  '/product-service/products*': '/products$1',
  '/product-service/products/:id': '/products/data/:id',
  '/product-service/products/search?query=:id': '/products/data?id=id',
  '/cart-service/cart': '/cart',
  '/cart-service/cart/:id': '/cart/:id',
  '/user-service/auth/login': '/login',
  '/user-service/address': '/address',
  '/user-service/address/:id': '/address/:id',
  '/category-service/': '/categories',
  '/menus': '/menus',
  '/products/:id': '/products/data/:id'
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
