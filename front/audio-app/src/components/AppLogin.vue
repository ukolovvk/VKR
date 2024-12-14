<template>
  <div class="login-container d-flex justify-content-center align-items-center min-vh-100">
    <div class="card p-4 shadow-lg" style="width: 300px; background-color: #1c1c1c; color: #f5f5f5;">
      <h3 class="text-center mb-4">Login</h3>
      <form @submit.prevent="login" class="needs-validation" novalidate>
        <div class="mb-3">
          <label for="username" class="form-label">Username:</label>
          <input type="text" v-model="username" class="form-control bg-dark text-light" id="username" required />
          <div class="invalid-feedback">
            Please enter your username.
          </div>
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">Password:</label>
          <input type="password" v-model="password" class="form-control bg-dark text-light" id="password" required />
          <div class="invalid-feedback">
            Please enter your password.
          </div>
        </div>
        <button type="submit" class="btn btn-primary w-100">Login</button>
      </form>
      <p class="text-center mt-3">
        <router-link to="/register" class="text-light">Register here</router-link>
      </p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: ''
    };
  },
  methods: {
    async login() {
      try {
        const response = await fetch('http://localhost:7080/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: this.username,
            password: this.password
          }),
          credentials: 'include'
        });
        if (response.ok) {
          this.$router.push('/upload');
        } else {
          alert('Login failed!');
        }
      } catch (error) {
        console.error('Error during login:', error);
      }
    }
  }
};
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  background-color: #000;
}

.login-container {
  background-color: #000;
}

.card {
  border-radius: 10px;
  background-color: #1c1c1c;
}

.form-control {
  background-color: #333;
  color: #f5f5f5;
}

.form-control:focus {
  box-shadow: none;
  border-color: #555;
}
</style>