# 🏦 **Gestión de Clientes Bancarios**

## 🎯 **Descripción del Proyecto**

Este proyecto consiste en desarrollar una aplicación en **Java** para la **gestión de clientes** de un banco y sus **tarjetas de crédito**. El sistema permitirá realizar operaciones de **alta, baja, modificación y consulta (CRUD)** sobre los clientes y sus tarjetas, además de ofrecer otras funcionalidades avanzadas como:

- **Caché LRU** para optimizar el rendimiento.
- **Validación** de datos antes de almacenar clientes y tarjetas.
- **Importación y exportación** de datos en formatos **JSON** y **CSV**.
- **Notificaciones** automáticas sobre cambios en los clientes.
- Gestión de **errores y excepciones** en caso de fallos.

## 📌 **Características Principales**

- CRUD completo para **Clientes** y **Tarjetas de Crédito**.
- **API REST** externa para obtener usuarios desde [`jsonplaceholder`](https://jsonplaceholder.typicode.com/users).
- **Base de datos PostgreSQL** para almacenar tarjetas.
- **SQLite local** para almacenamiento de clientes.
- **Notificaciones** sobre cualquier cambio en los datos de los clientes.
- **Logs** detallados de las operaciones realizadas.
- **Configuración parametrizada** en archivos `.env` y `application.properties`.

## 🚀 **Requisitos del Proyecto**

- Uso de **Spring Boot** como framework principal.
- Despliegue de la infraestructura con **Docker** (incluyendo **multi-stage build**).
- Pruebas unitarias y de integración usando **TestContainers**.
- Integración continua con **GitFlow** y Pull Requests para cada tarea.
