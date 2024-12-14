import { createRouter, createWebHistory } from 'vue-router';
import AppLogin from '../components/AppLogin.vue';
import AppRegister from '../components/AppRegister.vue';
import AppMain from '../components/AppMain.vue';


const routes = [
    { path: '/login', component: AppLogin },
    { path: '/register', component: AppRegister },
    { path: '/upload', component: AppMain },
    { path: '/', redirect: '/login' }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;