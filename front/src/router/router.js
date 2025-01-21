import { createRouter, createWebHistory } from 'vue-router';
import ChatPage from "@/components/Chat"
import RoomPage from "@/components/Room"
import ErrorPage from "@/components/ErrorPage"

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { 
            path: "/", 
            name: "Room", 
            component: RoomPage 
        },
        { 
            path: "/chat/:channelId", 
            name: "Chat",
            component: ChatPage ,
            props: true,
        },
        {
            path: "/:pathMatch(.*)",
            name: "not-found",
            component: ErrorPage
        }
    ]
});

export default router;