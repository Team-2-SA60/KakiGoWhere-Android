
## Production Server

KakiGoWhere is deployed on Digital Ocean, and will be kept running until 31st August 2025.

You can access our deployed Web Application [here](http://206.189.43.202/admin/login)

## 🛠️ Getting started

Pre-requisite:
- Install Android Studio [here](https://developer.android.com/studio)
- (If running locally) Get all containers running by following steps on
    - [KakiGoWhere-Backend](https://github.com/Team-2-SA60/KakiGoWhere-Backend)
    - [KakiGoWhere-Frontend](https://github.com/Team-2-SA60/KakiGoWhere-Frontend)
    - [KakiGoWhere-ML](https://github.com/Team-2-SA60/KakiGoWhere-ML)

---

1. Open terminal / command prompt and change directory to KakiGoWhere

    ```
    cd KakiGoWhere
    ```

2. Clone repository

    ```
    git clone https://github.com/Team-2-SA60/KakiGoWhere-Android.git
    ```

3. Access ApiConstants.kt
   ```
   KakiGoWhere
       ├── KakiGoWhere-Android
       │   ├── app
       │   │   ├── src
       │   │   │   ├── main
       │   │   │   │   ├── java
       │   │   │   │   │     ├── team2.kakigowhere
       │   │   │   │   │     │           ├── data
       │   │   │   │   │     │           │     ├── api
       │   │   │   │   │     │           │     │    └── ApiConstants.kt
   ```

> If running locally (default), change URL = "http://10.0.2.2"
> If running on production server, change URL = "http://206.189.43.202"

3. Start an Android emulator

4. Click Run ▶️ in Android Studio’s toolbar