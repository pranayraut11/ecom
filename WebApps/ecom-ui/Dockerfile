# Stage 1
# Pull node image and assign name to node image - build-stage
FROM node:lts-alpine3.18 as build-stage
# switch to app directory
WORKDIR /app
# Copy the package.json and package-lock.json to app folder
COPY package*.json /app/
# Install all dependencies 
RUN npm install
# Copy all code to app folder
COPY ./ /app/

ARG configuration=development
# Build final deployable angular project and set base ref - ecom
RUN npm run build -- --output-path=./dist/ --configuration $configuration --base-href /ecom/

# Stage 2 
# Pull Nginx image
FROM nginx:alpine
#Copy dist folder to htlm folder
COPY --from=build-stage /app/dist/ /usr/share/nginx/html 
#Copy default nginx configuration
COPY ./nginx-custom.conf /etc/nginx/conf.d/default.conf