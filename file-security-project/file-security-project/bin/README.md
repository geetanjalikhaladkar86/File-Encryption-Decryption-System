File Security Project (minimal)

How to run:
1. Install Java 17 and Maven.
2. From project root run: mvn spring-boot:run
3. Open http://localhost:8080/register.html to register a user.
4. Then login via http://localhost:8080/login.html
5. Use dashboard to encrypt/decrypt folders (supported in Chromium browsers).

Notes:
- This is a minimal demo. Key is hardcoded for demo only. Do NOT use in production.
- H2 in-memory DB is used for simplicity.
