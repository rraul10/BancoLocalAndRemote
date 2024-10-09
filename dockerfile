# Etapa de compilación, un docker especifico, que se etiqueta como build
FROM gradle:jdk21 AS build

# Directorio de trabajo
WORKDIR /app

# Copia los archivos build.gradle y src de nuestro proyecto
COPY build.gradle.kts .
COPY gradlew /app/gradlew
COPY gradle gradle
COPY src src

# Verificar contenido y permisos después de copiar archivos
RUN echo "Contenido de /app después de copiar archivos:"
RUN ls -la /app
RUN echo "Permisos del archivo gradlew:"
RUN ls -la /app/gradlew

# Asegúrate de que el archivo gradlew tiene permisos de ejecución
RUN chmod +x /app/gradlew

# Verificación adicional de que el archivo gradlew es ejecutable
RUN echo "Verificando permisos de gradlew:"
RUN ls -la /app/gradlew

# Crear y ejecutar un script básico para comprobar si funciona
RUN echo -e '#!/bin/sh\n\n echo "Hola, Docker!"' > /app/test.sh
RUN chmod +x /app/test.sh
RUN /bin/sh /app/test.sh

# Compilar el proyecto usando `/bin/sh`
RUN echo "Ejecutando gradlew build" && /bin/sh -c "/app/gradlew build"

# Etapa de ejecución, un docker especifico, que se etiqueta como run
# Con una imagen de java, solo necesitamos el jre
FROM eclipse-temurin:21-jre-alpine AS run

# Directorio de trabajo
WORKDIR /app

# Copia el jar de la aplicación, ojo que esta en la etapa de compilación, etiquetado como build
# Cuidado con la ruta definida cuando has copiado las cosas en la etapa de compilación
# Para copiar un archivo de una etapa a otra, se usa la instrucción COPY --from=etapaOrigen
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/my-app.jar

# Ejecuta el jar
ENTRYPOINT ["java","-jar","/app/my-app.jar"]
