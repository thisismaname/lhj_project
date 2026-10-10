import request from "axios"
import {message} from 'ant-design-vue'
import router from '../router/index'

// const token = localStorage.getItem("token")

const service = request.create({
    baseURL: 'http://localhost:8081/api',
    timeout: 50000,
});

// 2. 请求拦截器：每次请求前动态获取最新 Token
service.interceptors.request.use(
    (config) => {
        // 动态从 localStorage 获取 token
        const token = localStorage.getItem("token");

        if (token) {
            // 标准格式：Bearer <token>
            // 注意：确保 key 是 'Authorization' (大小写敏感，通常首字母大写)
            config.headers['Authorization'] = `${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

service.interceptors.response.use(
    response => {

        const res = response.data;

        //判断response状态
        if (!res.status) message.error('请求错误: ' + res.msg)

        if (res.code === 403) router.push("/403")

        return res
    },

    error => {
        message.error(error)
        router.push('/500')
        return Promise.reject(error)
    }
);

export default service