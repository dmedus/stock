FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
COPY "target/stock-ventas-1.0.jar" "app.jar"
EXPOSE 8090
ENTRYPOINT [ "java","-jar","app.jar" ]