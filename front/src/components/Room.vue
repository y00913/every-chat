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

    <div class="black-bg" v-show="enterState">
        <div class="white-bg">
            <p v-show="!enterFail">비밀번호를 입력해주세요.</p>
            <p v-show="enterFail">비밀번호가 틀렸습니다.</p>
            <input v-model="pw" type="text" required>
            <p></p>
            <button @click="checkRoomPw" v-on:keyup.enter="submit">확인</button>
            <button @click="handleEnterPop">취소</button>
        </div>
    </div>

    <div class="chat-box">
        <tr v-for="(item, idx) in roomList" :key="idx" class="room-list">
            <div>
                <td style="width:200px;">
                    {{ item.createAt.substring(0, item.createAt.indexOf('T')) }}
                </td>
                <td style="width:600px;">
                    {{ item.channelName }}
                    <!-- <div v-show="item.isLock" style="width:10px; height:10px"> <img style="object-fit:cover" :src="`https://cdn-icons-png.flaticon.com/512/5321/5321786.png`"> </div> -->
                </td>
                <td style="width:100px;">
                    <!-- <router-link :to="{ name: 'Chat', params: { channelId: item.id, channelName: item.channelName } }">
                        <button>입장</button>
                    </router-link> -->
                    <button @click="handleEnterPop(), getRoomInfo(item.id, item.channelName), enterRoom(item.id, item.channelName, item.isLock)">
                        입장
                    </button>
                </td>
                <td style="width:100px;">
                    <button @click="handleDeletePop(), getRoomInfo(item.id, item.channelName)">
                        삭제
                    </button>
                </td>
            </div>
        </tr>
    </div>
    <div>
        <button @click="handleCreatePop">생성</button>
    </div>
    <div>
        <tr style="width:1000px;">
            <td style="width:200px;">
                <button v-show="!(pageNum == 0)" @click="getRoom(pageNum - 1)">이전</button>
            </td>
            <td style="width:50px">
                {{ pageNum + 1 }} / {{ pageSize }}
            </td>
            <td style="width:200px">
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
            isLock: false,
            lockPw: "",
            enterState: false,
            enterFail: false,
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
        },
        handleCreatePop() {
            this.createState = !this.createState;
        },
        async createRoom() {
            if (this.roomName == "") return;

            await axios.post(this.url + "/channel", {
                channelName: this.roomName,
                pw: this.pw,
                ip: this.ip,
                isLock: this.isLock,
                lockPw: this.lockPw,
            });

            this.roomName = "";
            this.pw = "";
            this.isLock = false;
            this.lockPw = "";

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

            if (response.data.message == '비밀번호가 틀렸습니다.') {
                this.deleteFail = true;
                this.pw = "";
                return;
            }

            this.getRoom(0);
            this.handleDeletePop();
        },
        handleDeletePop() {
            this.deleteState = !this.deleteState;
            this.deleteFail = false;
        },
        getRoomInfo(roomId, roomName) {
            this.roomId = roomId;
            this.roomName = roomName;
        },
        enterRoom(channelId, channelName, isLock){
            if(!isLock) {
            this.$router.push({ name: 'Chat', params: { channelId: channelId, channelName: channelName } });
            }
        },
        async checkRoomPw(){
            const response = await axios.get(this.url + "/channel/lock",
            {
                headers: {
                    'channel-id': this.roomId,
                    'lock-pw': this.pw,
                }
            }
            );

            this.pw = "";

            if(!response.data.data) {
                this.enterFail = true;
                return;
            }

            this.handleEnterPop();
            this.$router.push({ name: 'Chat', params: { channelId: this.roomId, channelName: this.roomName } });
        },
        handleEnterPop() {
            this.enterState = !this.enterState;
            this.enterFail = false;
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
    height: 68px;
}
</style>