<template>
    <div class="chat-box">
        <div v-for="(item, idx) in roomList" :key="idx">
            <router-link :to="'/chat/' + item.channelName + '/' + item.id">{{ item.channelName }}</router-link>
        </div>
    </div>
    <div>
        <ul>
            <li class="previous-page">
                <button v-show="!(pageNum == 0)" 
                @click="getRoom(pageNum - 1)">이전</button>
            </li>
            <li class="page-count">
                {{ pageNum + 1 }} / {{ pageSize }}
            </li>
            <li class="next-page">
                <button v-show="!(pageNum == pageSize - 1)"
                @click="getRoom(pageNum + 1)">다음</button>
            </li>
        </ul>
    </div>
</template>

<script>
import axios from "axios"

export default {
    name: 'App',
    data() {
        return {
            url: "http://everychat.kro.kr:8082",
            roomList: [],
            pageNum: 0,
            pageSize: 5
        }
    },
    created() {
        this.getRoom(0);
    },
    methods: {
        async getRoom(pageNumber) {
            this.pageNum = pageNumber;
            const response = await axios.get(this.url + "/channel/" + pageNumber);
            // this.pageSize = response.data.data.pageSize;

            this.roomList = [];
            this.roomList.push(...response.data.data.channelList);

            console.log(pageNumber + '페이지 이동');
        }
    }
}
</script>

<style>
@import "../assets/css/MainBox.css";

li {
    display: inline-block;
    margin: 3px;
    position: fixed;
}

.page-count {
    left: 50%;
    transform: translate(-60%, 0);
}

.previous-page {
    left: 45%;
}

.next-page {
    right: 45%;
}
</style>