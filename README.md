# Tongue - Shopping Service

Shopping service based on Uber Eats backend for customers and merchants. Merchants can register their stores on ShoppingService and publish their products, group them in families, apply any discount available from a wide set of discount features, add modifiers to a unit or a group of products, such as 'extra ketchup', 'colors' and so on. 

Once a catalog of products has been registered, a customer can access to the published stores by using any web or mobile client such as Tongue Customers App. 

## Features

- Developed using Spring Boot

- Customers Managment

- Merchants Managment

- Shopping Cart

- Checkout 

- Integration with Tongue Customers App (Android App)

- REST services

- Google Authentication with JWT

- Catalogue

- Uber Eats based service

- Modifiers and discounts for products

- AMQP

- Unit and Integration tests

- Documentation


## How it works

This service provides a set of endpoints to be accessed from a web page or mobile client such as Tongue Customers App, some of the endpoints that you might call are GET : shopping/storevariants to obtain a JSON array of available stores, GET: /shipping/collections?storeVariantId can be used to get the list of available collections of products provided by the merchant of the store.

For Merchants a web page built in React is going to be used to contact Shopping Service and publish their products.

## Installation (Docker)

Clone the repository:

```bash
gh repo clone alexanderommel/ShoppingService-Tongue
```

Move to the root directory of the project, and run the following line:

```bash
docker-compose up 
```

If you modify the source code, use the following command to rebuild the .jar:

```bash
maven clean package
```

