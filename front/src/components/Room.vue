<template>
    <div class="black-bg" v-show="popState">
        <div class="white-bg">
            <p>방 제목을 입력해주세요.</p>
            <input v-model="roomName" type="text" required>
            <p></p>
            <button @click="createRoom" v-on:keyup.enter="submit">확인</button>
            <button class="closePop" @click="handlePop">취소</button>
        </div>
    </div>

    <div class="chat-box">
        <tr v-for="(item, idx) in roomList" :key="idx" class="room-list">
            <td style="width:5vw;">
                {{ item.createAt.substring(0, item.createAt.indexOf('T')) }}
            </td>
            <td style="width:15vw">
                <router-link :to="{name: 'Chat', params: {channelId: item.id, channelName: item.channelName}}">
                    <button>{{ item.channelName }}</button>
                </router-link>
            </td>
        </tr>
    </div>
    <div>
        <button @click="handlePop">생성</button>
    </div>
    <div>
        <ul>
            <li class="previous-page">
                <button v-show="!(pageNum == 0) && !popState" @click="getRoom(pageNum - 1)">이전</button>
            </li>
            <li class="page-count">
                {{ pageNum + 1 }} / {{ pageSize }}
            </li>
            <li class="next-page">
                <button v-show="!(pageNum == pageSize - 1) && !popState" @click="getRoom(pageNum + 1)">다음</button>
            </li>
        </ul>
    </div>
</template>

<script>
import axios from "axios";

export default {
    name: 'App',
    data() {
        return {
            url: "http://everychat.kro.kr:8082",
            // url: "http://localhost:8080",
            roomList: [],
            pageNum: 0,
            pageSize: 5,
            popState: false,
            roomName: "",
            ip: "",
        }
    },
    created() {
        this.getRoom(0);
        this.findMyIp();
    },
    methods: {
        async getRoom(pageNumber) {
            this.pageNum = pageNumber;
            const response = await axios.get(this.url + "/channel/" + pageNumber);
            this.pageSize = response.data.data.pageSize;

            this.roomList = [];
            this.roomList.push(...response.data.data.channelList);

            console.log(pageNumber + '페이지 이동');
        },
        handlePop() {
            this.popState = !this.popState;
        },
        async createRoom() {
            if (this.roomName == "") return;

            const response = await axios.post(this.url + "/channel", {
                channelName: this.roomName,
                ip: this.ip,
            });

            console.log(response.data);

            this.getRoom(0);
            this.handlePop();
        },
        async findMyIp() {
            const response = await axios.get('https://ipwho.is');
            this.ip = response.data.ip;
        }
    },
    components: {
    }
}
</script>

<style>
@import "../assets/css/MainBox.css";
@import "../assets/css/BlackBg.css";
@import "../assets/css/WhiteBg.css";

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

.room-list {
    height: 5.7vh;
}
</style>