FROM node:12.4.0-alpine as build
WORKDIR /app
COPY . /app
RUN npm install
RUN npm run build

FROM nginx:1.17.0-alpine
COPY --from=build /app/build /var/www
COPY deploy/docker/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
ENTRYPOINT ["nginx","-g","daemon off;"]
