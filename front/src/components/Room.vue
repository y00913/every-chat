<template>
    <div class="black-bg" v-show="createState">
        <div class="white-bg" @keyup.enter="createRoom">
            <div v-show="duplicatedName" style="margin-bottom:30px;">
                <p>방 제목이 이미 존재합니다.</p>
            </div>
            <div>
            <p>방 제목을 입력해주세요.</p>
            <input v-model="roomName" type="text">
            <p>삭제 비밀번호를 입력해주세요.</p>
            <input v-model="pw" type="text">
            <p>입장 비밀번호를 원할 시 클릭해주세요.</p>
            <input type="checkbox" v-model="isLock">
            <div v-show="isLock">
                <p>입장 비밀번호를 입력해주세요.</p>
                <input type="password" v-model="lockPw">
            </div>
            <p></p>
            <button @click="createRoom" style="margin-right:10px;">확인</button>
            <button @click="handleCreatePop">취소</button>
            </div>
        </div>
    </div>

    <div class="black-bg" v-show="deleteState">
        <div class="white-bg" @keyup.enter="deleteRoom">
            <p v-show="!deleteFail">삭제 비밀번호를 입력해주세요.</p>
            <p v-show="deleteFail">삭제 비밀번호가 틀렸습니다.</p>
            <input v-model="pw" type="text" required>
            <p></p>
            <button @click="deleteRoom" style="margin-right:10px;">확인</button>
            <button @click="handleDeletePop">취소</button>
        </div>
    </div>

    <div class="black-bg" v-show="enterState">
        <div class="white-bg" @keyup.enter="checkRoomPw">
            <p v-show="!enterFail">입장 비밀번호를 입력해주세요.</p>
            <p v-show="enterFail">입장 비밀번호가 틀렸습니다.</p>
            <input v-model="pw" type="password" required>
            <p></p>
            <button @click="checkRoomPw" style="margin-right:10px;">확인</button>
            <button @click="handleEnterPop">취소</button>
        </div>
    </div>

    <div>
        <tr style="width:1000px;">
            <td style="width:230px;">

            </td>
            <td style="width:660px;">
                <div @keyup.enter="getRoomByName(0)">
                    <input v-model="serachName" type="text" required style="width:200px;">
                    <button @click="getRoomByName(0)" style="margin-left:5px;">검색</button>
                </div>
            </td>
            <td style="width:200px">
                <div>
                    <button @click="handleCreatePop">방 생성</button>
                </div>
            </td>
        </tr>
    </div>

    <div class="chat-box">
        <div v-show="roomList.length == 0">
            <h4>방이 없습니다.</h4>
        </div>
        <tr v-for="(item, idx) in roomList" :key="idx" class="room-list">
            <div>
                <td style="width:200px;">
                    {{ item.createAt.substring(0, item.createAt.indexOf('T')) }}
                </td>
                <td style="width:600px;">
                    {{ item.channelName }}
                </td>
                <td style="width:20px;">
                    <div v-show="item.isLock">
                        <img style="width:20px; height:20px;"
                            :src="`https://cdn.icon-icons.com/icons2/3450/PNG/512/secure_safety_password_protection_security_lock_padlock_icon_219355.png`">
                    </div>
                </td>
                <td style="width:80px;">
                    <!-- <router-link :to="{ name: 'Chat', params: { channelId: item.id, channelName: item.channelName } }">
                        <button>입장</button>
                    </router-link> -->
                    <button
                        @click="handleEnterPop(), getRoomInfo(item.id, item.channelName), enterRoom(item.id, item.channelName, item.isLock)">
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

    <div style="height:30px;">
        <tr style="width:1000px;">
            <td style="width:200px;">
                <button v-show="!(pageNum == 0) && !(roomList.length == 0)"
                    @click="search ? getRoomByName(pageNum - 1) : getRoom(pageNum - 1)">이전</button>
            </td>
            <td style="width:50px;">
                <p style="margin-top: 15px;">
                    {{ pageNum + 1 }} / {{ pageSize }}
                </p>
            </td>
            <td style="width:200px">
                <button v-show="!(pageNum == pageSize - 1) && !(roomList.length == 0)"
                    @click="search ? getRoomByName(pageNum + 1) : getRoom(pageNum + 1)">다음</button>
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
            url: "https://everychat.kro.kr",
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
            search: false,
            serachName: "",
            duplicatedName: false,
        }
    },
    created() {
        this.getRoom(0);
        this.findMyIp();
    },
    methods: {
        async getRoom(pageNumber) {
            this.pageNum = pageNumber;
            const response = await axios.get(this.url + "/api/channel/" + pageNumber);
            this.pageSize = response.data.data.pageSize;

            this.roomList = [];
            this.roomList.push(...response.data.data.channelList);

            if (this.roomList.length == 0) this.pageNum = -1;
        },
        async getRoomByName(pageNumber) {
            if (this.serachName == "") {
                this.getRoom(0);
                this.search = false;
                return;
            }

            this.pageNum = pageNumber;
            const response = await axios.get(this.url + "/api/channel/" + this.serachName + "/" + pageNumber);
            this.pageSize = response.data.data.pageSize;

            this.search = true;
            this.roomList = [];
            this.roomList.push(...response.data.data.channelList);

            if (this.roomList.length == 0) this.pageNum = -1;
        },
        handleCreatePop() {
            this.createState = !this.createState;
            this.initData();
        },
        async createRoom() {
            if (this.roomName == "") return;
            if (this.pw == "") return;
            if (this.isLock && this.lockPw == "") return;

            this.duplicatedName = await this.checkExistName();
            if (this.duplicatedName) {
                return;
            }

            const response = await axios.post(this.url + "/api/channel", {
                channelName: this.roomName,
                pw: this.pw,
                ip: this.ip,
                isLock: this.isLock,
                lockPw: this.lockPw,
            });

            const data = response.data.data;

            this.enterRoom(data.id, data.channelName, false);
        },
        async findMyIp() {
            const response = await axios.get('https://ipwho.is');
            this.ip = response.data.ip;
        },
        async deleteRoom() {
            if (this.pw == "") return;

            const response = await axios.delete(this.url + "/api/channel",
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
            this.initData();
        },
        getRoomInfo(roomId, roomName) {
            this.roomId = roomId;
            this.roomName = roomName;
        },
        enterRoom(channelId, channelName, isLock) {
            if (!isLock) {
                this.$router.push({ name: 'Chat', params: { channelId: channelId, channelName: channelName } });
            }
        },
        async checkRoomPw() {
            if (this.pw == "") return;

            const response = await axios.get(this.url + "/api/channel/lock",
                {
                    headers: {
                        'channel-id': this.roomId,
                        'lock-pw': this.pw,
                    }
                }
            );

            this.pw = "";

            if (!response.data.data) {
                this.enterFail = true;
                return;
            }

            this.$router.push({ name: 'Chat', params: { channelId: this.roomId, channelName: this.roomName } });
        },
        handleEnterPop() {
            this.enterState = !this.enterState;
            this.enterFail = false;
            this.initData();
        },
        initData() {
            this.roomName = "";
            this.pw = "";
            this.isLock = false;
            this.lockPw = "";
            this.duplicatedName = false;
        },
        async checkExistName() {
            const response = await axios.get(this.url + "/api/channel/name/" + this.roomName);
            return response.data.data;
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