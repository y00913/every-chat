<template>
    <div class="chat-box scrollbar">
        <div v-for="(item, idx) in roomList" :key="idx">
            <router-link :to="'/chat/' + item.channelName + '/' + item.id">{{ item.channelName }}</router-link>
        </div>
    </div>
</template>

<script>
import axios from "axios"

export default {
    name: 'App',
    data() {
        return {
            url: "http://everychat.kro.kr:8082",
            roomList: []
        }
    },
    created() {
        this.getRoom();
    },
    methods: {
        async getRoom() {
            const response = await axios.get(this.url + "/channel");

            this.roomList.push(...response.data.data);
        }
    }
}
</script>

<style>
@import "../assets/css/MainBox.css";
</style>