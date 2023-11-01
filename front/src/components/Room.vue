<template>
    <div class="black-bg" v-show="createState">
        <div class="white-bg">
            <p>방 제목을 입력해주세요.</p>
            <input v-model="roomName" type="text" required>
            <p>비밀번호를 입력해주세요.</p>
            <input v-model="pw" type="text" required>
            <p></p>
            <button @click="createRoom" v-on:keyup.enter="submit">확인</button>
            <button @click="handleCreatePop">취소</button>
        </div>
    </div>

    <div class="black-bg" v-show="deleteState">
        <div class="white-bg">
            <p v-show="!deleteFail">비밀번호를 입력해주세요.</p>
            <p v-show="deleteFail">비밀번호가 틀렸습니다.</p>
            <input v-model="pw" type="text" required>
            <p></p>
            <button @click="deleteRoom" v-on:keyup.enter="submit">확인</button>
            <button @click="handleDeletePop">취소</button>
        </div>
    </div>

    <div class="chat-box">
        <tr v-for="(item, idx) in roomList" :key="idx" class="room-list">
            <td style="width:5vw;">
                {{ item.createAt.substring(0, item.createAt.indexOf('T')) }}
            </td>
            <td style="width:15vw">
                <router-link :to="{ name: 'Chat', params: { channelId: item.id, channelName: item.channelName } }">
                    <button>{{ item.channelName }}</button>
                </router-link>
            </td>
            <td>
                <button @click="handleDeletePop(), getRoomId(item.id)">
                    X
                </button>
            </td>
        </tr>
    </div>
    <div>
        <button @click="handleCreatePop">생성</button>
    </div>
    <div>
        <tr style="width:25vw">
            <td style="width:10vw;">
                <button v-show="!(pageNum == 0)" @click="getRoom(pageNum - 1)">이전</button>
            </td>
            <td style="width:5">
                {{ pageNum + 1 }} / {{ pageSize }}
            </td>
            <td style="width:10vw">
                <button v-show="!(pageNum == pageSize - 1)" @click="getRoom(pageNum + 1)">다음</button>
            </td>
        </tr>
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
            createState: false,
            deleteState: false,
            roomName: "",
            ip: "",
            roomId: "",
            pw: "",
            deleteFail: false,
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
        handleCreatePop() {
            this.createState = !this.createState;
        },
        async createRoom() {
            if (this.roomName == "") return;

            const response = await axios.post(this.url + "/channel", {
                channelName: this.roomName,
                pw: this.pw,
                ip: this.ip,
            });

            this.pw = "";

            console.log(response.data);

            this.getRoom(0);
            this.handleCreatePop();
        },
        async findMyIp() {
            const response = await axios.get('https://ipwho.is');
            this.ip = response.data.ip;
        },
        async deleteRoom() {
            const response = await axios.delete(this.url + "/channel", 
            {
                headers: {
                    'channel-id': this.roomId,
                    'pw': this.pw,
                }
            });

            console.log(response);

            if(response.data.message == '비밀번호가 틀렸습니다.') {
                this.deleteFail = true;
                return;
            }

            this.getRoom(0);
            this.handleDeletePop();
        },
        handleDeletePop() {
            this.deleteState = !this.deleteState;
            this.deleteFail = false;
        },
        getRoomId(roomId){
            this.roomId = roomId;
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