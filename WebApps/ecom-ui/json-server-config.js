const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('json-server-db.json')

const middlewares = jsonServer.defaults()

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
  
  // Continue to JSON Server router
  next()
})

//Add routing url 
server.use(jsonServer.rewriter({
  '/product-service/product': '/product',
  '/product-service/product/:id': '/product/:id',
  '/product-service/product?q=:q': '/product?q=:q',
  '/cart-service/cart': '/cart',
  '/cart-service/cart/:id': '/cart/:id',
  '/user-service/auth/login': '/login',
  '/user-service/address' : '/address',
  '/user-service/address/:id' : '/address/:id',
  '/category-service/' : '/categories'
}))

server.use(router)


server.listen(3000, () => {
  console.log('JSON Server is running on PORT 3000 ')
})