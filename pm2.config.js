module.exports = {
    apps: [
        {
            name: 'auth',
            args: [
                "-jar",
                "build/libs/jalgoarena-auth-2.1.0-SNAPSHOT.jar"
            ],
            script: 'java',
            env: {
                PORT: 5003
            }
        }
    ]
};
