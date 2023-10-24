import { createRouter, createWebHistory } from 'vue-router';
import ChatPage from "@/components/Chat"
import ErrorPage from "@/components/ErrorPage"

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: "/", name: "chat", component: ChatPage },
        {
            path: "/:pathMatch(.*)",
            name: "not-found",
            component: ErrorPage
        },
    ]
});

export default router;