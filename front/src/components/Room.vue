<template>
    <div class="black-bg" v-show="createState">
        <div class="white-bg" @keyup.enter="createRoom">
            <div v-show="duplicatedName" style="margin-bottom:30px;">
                <p>방 제목이 이미 존재합니다.</p>
            </div>
            <div>
            <p v-show="!roomNameError">방 제목을 입력해주세요.</p>
            <p style="color: #E84A5B;" v-show="roomNameError">20자 내로 입력해주세요 !</p>
            <input :spellcheck="false" v-model="roomName" type="text" placeholder="방 제목">
            <p>방 비밀번호를 입력해주세요.</p>
            <input v-model="pw" type="password" placeholder="방 비밀번호">
            <p>입장 비밀번호를 원할 시 클릭해주세요.</p>
            <input type="checkbox" v-model="isLock">
            <div v-show="isLock">
                <p>입장 비밀번호를 입력해주세요.</p>
                <input v-model="lockPw" type="password" placeholder="입장 비밀번호">
            </div>
            <p></p>
            <button @click="createRoom" style="margin-right:10px;">확인</button>
            <button @click="handleCreatePop">취소</button>
            </div>
        </div>
    </div>

    <div class="black-bg" v-show="deleteState">
        <div class="white-bg" @keyup.enter="deleteRoom">
            <p v-show="!deleteFail">방 비밀번호를 입력해주세요.</p>
            <p v-show="deleteFail">방 비밀번호가 틀렸습니다.</p>
            <input v-model="pw" type="password" placeholder="방 비밀번호" required>
            <p></p>
            <button @click="deleteRoom" style="margin-right:10px;">확인</button>
            <button @click="handleDeletePop">취소</button>
        </div>
    </div>

    <div class="black-bg" v-show="enterState">
        <div class="white-bg" @keyup.enter="checkRoomPw">
            <p v-show="!enterFail">입장 비밀번호를 입력해주세요.</p>
            <p v-show="enterFail">입장 비밀번호가 틀렸습니다.</p>
            <input v-model="lockPw" type="password" placeholder="입장 비밀번호" required>
            <p></p>
            <button @click="checkRoomPw" style="margin-right:10px;">확인</button>
            <button @click="handleEnterPop">취소</button>
        </div>
    </div>

    <table style="width:95%; max-width:1000px; table-layout:fixed;">
        <tr >
            <td style="text-align: left;">
                <div @keyup.enter="handleNickname" class="div-left">
                    <input :spellcheck="false" v-model="sender" type="text" placeholder="닉네임" class="input-nickname">
                    <button 
                        @click="handleNickname" 
                        class="sender-button" 
                        :style="{ marginLeft: '5px', pointerEvents: showSaveMessage || senderError ? 'none' : 'auto' }"
                        :disabled="showSaveMessage || senderError"
                    >
                        <a v-if="!showSaveMessage && !senderError">저장</a>
                        <a v-if="showSaveMessage" :style="{ color: messageColor }">완료</a>
                        <a v-if="senderError" style="color: #E84A5B;">길어요</a>
                    </button>
                </div>
            </td>
            <td style="text-align: right;">
                <div class="div-right">
                    <span @keyup.enter="getRoomByName(0)" class="span-search">
                        <input :spellcheck="false" v-model="serachName" type="text" required class="input-search" v-show="isSearch" placeholder="제목 입력">
                        <img @click="handleSearchBar()" class="search-img" src="@/assets/img/search.png" />
                    </span>
                    <span class="span-add">
                        <img @click="handleCreatePop()" class="add-img" src="@/assets/img/add_chat.png" />
                    </span>
                </div>
            </td>
        </tr>
    </table>

    <div class="main-box">
        <div v-show="roomList.length == 0">
            <h4>방이 없습니다.</h4>
        </div>
        <tr v-for="(item, idx) in roomList" :key="idx" class="room-list">
            <div>
                <td style="width:200px;">
                    {{ formatDate(item.createAt) }}
                </td>
                <td style="width:600px;">
                    {{ item.channelName }}
                </td>
                <td style="width:20px;">
                    <div v-show="item.isLock">
                        <img style="width:20px; height:20px; margin:-5px;"
                        src="@/assets/img/lock.png" />
                    </div>
                </td>
                <td style="width:90px;">
                    <button
                        @click="handleEnterPop(), getRoomInfo(item.id, item.channelName), enterRoom(item.id, item.channelName, item.isLock)">
                        입장
                    </button>
                </td>
                <td style="width:90px;">
                    <button @click="handleDeletePop(), getRoomInfo(item.id, item.channelName)">
                        삭제
                    </button>
                </td>
            </div>
        </tr>
    </div>

    <div style="height:45px;">
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
            url: process.env.VUE_APP_SERVER_URL,
            roomList: [],
            pageNum: Number(localStorage.getItem('pageNum')) || 0,
            pageSize: 5,
            createState: false,
            deleteState: false,
            roomName: "",
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
            sender: localStorage.getItem('sender'),
            showSaveMessage: false,
            messageColor: 'green',
            isSearch: false,
            roomNameError: false,
            senderError: false,
        }
    },
    created() {
        this.getRoom(this.pageNum);
    },
    methods: {
        async getRoom(pageNumber) {
            this.pageNum = pageNumber;
            localStorage.setItem('pageNum', pageNumber);
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
            this.roomNameError = false;
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

            if (this.roomName.length > 20) {
                this.roomNameError = true;
                return;
            }

            const response = await axios.post(this.url + "/api/channel", {
                channelName: this.roomName,
                pw: this.pw,
                isLock: this.isLock,
                lockPw: this.lockPw,
            });

            const data = response.data.data;

            if(this.isLock) {
                this.roomId = data.id;
                this.checkRoomPw();
            } else {
                this.enterRoom(data.id, data.channelName, false);
            }
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
            if (this.lockPw == "") return;

            const response = await axios.get(this.url + "/api/channel/lock",
                {
                    headers: {
                        'channel-id': this.roomId,
                        'lock-pw': this.lockPw,
                    }
                }
            );

            this.lockPw = "";

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
        },
        handleNickname() {
            if (this.showSaveMessage) return;

            if (this.sender.length > 8) {
                this.senderError = true;
                setTimeout(() => {
                    this.senderError = false;
                }, 2000);
                return;
            }

            localStorage.setItem('sender', this.sender);

            this.showSaveMessage = true;
            this.messageColor = 'green';
            this.changeMessageColor();

            setTimeout(() => {
                this.showSaveMessage = false;
            }, 2000);
        },
        changeMessageColor() {
            const colors = ['LimeGreen', 'lightgreen', ''];
            let index = 0;

            const interval = setInterval(() => {
                this.messageColor = colors[index];
                index++;

                if (index >= colors.length) {
                    clearInterval(interval);
                }
            }, 2000 / colors.length);
        },
        handleSearchBar() {
            this.isSearch = !this.isSearch
        },
        handleBeforeUnload() {
            if (performance.navigation.type !== performance.navigation.TYPE_RELOAD) {
                this.resetPageNum();
            }
        },
        resetPageNum() {
            localStorage.setItem('pageNum', 0);
        },
        formatDate(date) {
            const datePart = date.substring(0, date.indexOf('T')) || date;
            const [year, month, day] = datePart.split('-');

            if (window.innerWidth <= 767) {
                const shortYear = year.substring(2);
                return `${shortYear}-${month}-${day}`;
            } else {
                return datePart;
            }
        }
    },
    components: {
    },
    mounted() {
        window.addEventListener('beforeunload', this.handleBeforeUnload);
    },
    beforeUnmount() {
        window.removeEventListener('beforeunload', this.handleBeforeUnload);
    },
    beforeRouteLeave() {
        this.handleBeforeUnload();
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
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.sender-button {
    width: 40px;
    height: 30px;
    padding: 0px;
    border: none;
}

.div-left {
    display: flex;
    border: 1px solid #ccc;
    border-radius: 5px;
    overflow: hidden;
    background-color: #fff;
    align-items: center;
    width: 170px;
}

.div-right {
    display: flex;
    align-items: center;
    justify-content: end;
}

.span-search {
    margin-right:25px;
    justify-content: center;
    display: flex;
}

.span-add {
    justify-content: center;
    display: flex;
}

.input-nickname {
    width:100px;
    border: none;
}

.input-search {
    width:200px;
    height:100%;
    margin-right: 20px;
    border: 1px solid #ccc;
    border-radius: 5px;
    padding: 3px 10px 3px 10px;
}

.add-img, .search-img {
    cursor:pointer;
    width:25px;
    height:25px;
}

@media (max-width: 767px) {
    .div-left {
        width: 100px;
    }

    .input-nickname {
        width: 40px;
    }

    .input-search {
        width: 25vw;
        margin-right: 7px;
    }
}
</style>
