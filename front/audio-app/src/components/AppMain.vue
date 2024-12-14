<template>
  <div class="container-fluid p-0 vh-100 d-flex flex-column bg-black">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container-fluid">
        <a class="navbar-brand" href="/path-to-audios">My Audios</a>
        <div class="d-flex align-items-center">
          <span class="text-white me-3">{{ userName }}</span>
          <button class="btn btn-outline-danger" type="button" @click="logout">Logout</button>
        </div>
      </div>
    </nav>

    <div class="d-flex justify-content-center align-items-center flex-grow-1">
      <form class="text-center p-4 rounded bg-dark" @submit.prevent="uploadAudio" style="max-width: 500px; width: 100%; color: #fff;">
        <h3 class="mb-3 text-white">Select <span>Audio</span></h3> <!-- Увеличенный размер и белый цвет для "Audio" -->
        <div class="mb-3">
          <input class="form-control" type="file" id="formFile" @change="handleFileUpload">
        </div>
        <button type="submit" class="btn" style="background-color: #ff8c00;">Upload</button> <!-- Рыжая кнопка -->
      </form>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie';
export default {
  name: 'AppMain',
  data() {
    return {
      userName: 'User Name', // Замените на динамические данные пользователя, если необходимо
    };
  },
  methods: {
    logout() {
      // Удаляем куки
      Cookies.remove('token');

      // Перенаправляем пользователя на страницу входа
      window.location.href = '/login';
    },
    handleFileUpload(event) {
      const file = event.target.files[0];
      console.log('File selected:', file.name);
      // Здесь можно добавить логику для обработки файла
    },
    uploadAudio() {
      console.log('Uploading audio...');
      // Логика отправки файла на сервер
    },
  },
};
</script>

<style scoped>
.vh-100, html, body {
  height: 100vh;
  overflow: hidden; /* Применяем к .vh-100 и к <html> и <body> */
  padding: 0;
  margin: 0;
}

.bg-black {
  background-color: #000; /* Черный цвет для заднего фона */
}

.bg-dark {
  background-color: #343a40 !important; /* Черный цвет для меню и формы */
}

.form-control, .btn {
  border-radius: 0.25rem;
}

.form-control {
  background-color: #333; /* Черный фон для поля ввода */
  border: 1px solid #666; /* Слегка выделяем границу */
  color: #fff; /* Белый текст внутри поля ввода */
}

.form-control::file-selector-button {
  background-color: #666; /* Темнее кнопка выбора файла */
  color: #fff;
  border-color: #333;
}

.btn {
  color: #fff;
}
</style>