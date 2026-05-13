# dan-football-stats

Iniciar los contenedores
docker compose up -d --build

Bajar los contenedores
docker compose down -v

Probar los endpoints de test
- http://localhost:8081/api/test/context
- http://localhost:8082/api/test/context
- http://localhost:8083/api/test/context


Con el API gateway configurado podemos acceder a las URL

http://localhost:8080/core-registry/api/test/context
http://localhost:8080/live-engine/api/test/context
http://localhost:8080/stats/api/test/context