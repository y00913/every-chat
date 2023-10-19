FROM node:18.16
EXPOSE 3000
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
CMD ["npm","run","serve"]