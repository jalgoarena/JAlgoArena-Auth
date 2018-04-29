module.exports = {
    apps: [
        {
            name: 'auth',
            args: [
                "-jar",
                "build/libs/jalgoarena-auth-2.0.0-SNAPSHOT.jar"
            ],
            script: 'java',
            env: {
                PORT: 5003,
                EUREKA_URL: 'http://localhost:5000/eureka/'
            }
        }
    ]
};
