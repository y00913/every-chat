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

    <table style="width:90%; max-width:1000px; table-layout:fixed;">
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
                        <input :spellcheck="false" v-model="searchName" type="text" required class="input-search" v-show="isSearch" placeholder="제목 입력 후 엔터">
                        <img @click="handleSearchBar()" class="search-img" src="@/assets/img/search.png"  alt="search"/>
                    </span>
                    <span class="span-add">
                        <img @click="handleCreatePop()" class="add-img" src="@/assets/img/add_chat.png"  alt="add"/>
                    </span>
                </div>
            </td>
        </tr>
    </table>

    <div class="main-box">
        <div v-show="roomList.length === 0">
            <h4>방이 없습니다.</h4>
        </div>
        <tr v-for="(item, idx) in roomList" :key="idx" class="room-list"
        @click="handleRoomInfoClick(item)" 
        @dblclick="handleRoomInfoDblClick(item)">
                <td style="width:200px;">
                    {{ formatDate(item.createAt) }}
                </td>
                <td class="room-title">
                    <a>
                        {{ item.channelName }}
                    </a>
                    <a v-if="item.hasNewChat" class="new-chatting">
                      NEW
                    </a>
                    <span v-show="item.isLock">
                      <img style="width:20px; height:20px; margin:-5px -5px -5px 3px;"
                           src="@/assets/img/lock.png"  alt="lock"/>
                    </span>
                </td>
                <td style="width:100px;">
                    {{ item.memberCount }} 명
                </td>
                <td style="width:100px;">
                    <img style="width:20px; height:20px; margin:-5px 3px -5px -5px; cursor: pointer;"
                    @click="handleDeletePop(), getRoomInfo(item.id, item.channelName)"
                    src="@/assets/img/delete.png"  alt="delete"/>
                </td>
        </tr>
    </div>

    <table style="height:6.5vh; user-select: none;">
        <tr style="width:1000px;">
            <td class="previous-button">
                <a v-show="!(pageNum === 0) && !(roomList.length === 0)" style="cursor: pointer;"
                    @click="search ? getRoomByName(pageNum - 1) : getRoom(pageNum - 1)">이전</a>
            </td>
            <td v-for="idx in visiblePages" :key="idx" style="width:50px;">
                <p style="margin-top: 15px; cursor: pointer;" 
                    :style="{ color: pageNum + 1 !== idx ? 'rgba(0, 0, 0, 0.5)' : 'black' }"
                    @click="search ? getRoomByName(idx - 1) : getRoom(idx - 1)">
                    {{ idx }}
                </p>
            </td>
            <td class="next-button">
                <a v-show="!(pageNum === pageSize - 1) && !(roomList.length === 0)" style="cursor: pointer;"
                    @click="search ? getRoomByName(pageNum + 1) : getRoom(pageNum + 1)">다음</a>
            </td>
        </tr>
    </table>
</template>

<script>
import axios from "axios";

export default {
    name: 'App',
    data() {
        return {
            serverUrl: process.env.VUE_APP_SERVER_URL,
            clientUrl: process.env.VUE_APP_CLIENT_URL,
            roomList: [],
            pageNum: Number(localStorage.getItem('pageNum')) || 0,
            pageSize: 10,
            maxPage: 5,
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
            searchName: "",
            duplicatedName: false,
            sender: localStorage.getItem('sender'),
            showSaveMessage: false,
            messageColor: 'green',
            isSearch: false,
            roomNameError: false,
            senderError: false,
            isMobile: localStorage.getItem('isMobile') === 'true',
            categoryId: 1
        }
    },
    created() {
        this.getRoom(this.pageNum);
        window.addEventListener("message", this.handleMessage);
    },
    methods: {
        async getRoom(pageNumber) {
            this.pageNum = pageNumber;
            localStorage.setItem('pageNum', pageNumber);
            const response = await axios.get(this.serverUrl + "/api/channel/" + this.categoryId + "/" + pageNumber);
            this.pageSize = response.data.data.pageSize;

            this.roomList = [];
            this.roomList.push(...response.data.data.channelList);

            if (this.roomList.length === 0) this.pageNum = -1;
        },
        async getRoomByName(pageNumber) {
            if (this.searchName === "") {
                await this.getRoom(0);
                this.search = false;
                return;
            }

            this.pageNum = pageNumber;
            const response = await axios.get(this.serverUrl + "/api/channel/" + this.categoryId + "/" + this.searchName + "/" + pageNumber);
            this.pageSize = response.data.data.pageSize;

            this.search = true;
            this.roomList = [];
            this.roomList.push(...response.data.data.channelList);

            if (this.roomList.length === 0) this.pageNum = -1;
        },
        handleCreatePop() {
            this.createState = !this.createState;
            this.roomNameError = false;
            this.initData();
        },
        async createRoom() {
            if (this.roomName === "") return;
            if (this.pw === "") return;
            if (this.isLock && this.lockPw === "") return;

            this.duplicatedName = await this.checkExistName();
            if (this.duplicatedName) {
                return;
            }

            if (this.roomName.length > 20) {
                this.roomNameError = true;
                return;
            }

            const response = await axios.post(this.serverUrl + "/api/channel", {
                channelName: this.roomName,
                pw: this.pw,
                isLock: this.isLock,
                lockPw: this.lockPw,
            });

            const data = response.data.data;

            if(this.isLock) {
                this.roomId = data.id;
                await this.checkRoomPw();
            } else {
                this.enterRoom(data.id, data.channelName, false);
            }
            this.handleCreatePop()
            window.location.reload();
        },
        async deleteRoom() {
            if (this.pw === "") return;

            const response = await axios.delete(this.serverUrl + "/api/channel",
                {
                    headers: {
                        'channel-id': this.roomId,
                        'pw': this.pw,
                    }
                });

            if (response.data.message === '비밀번호가 틀렸습니다.') {
                this.deleteFail = true;
                this.pw = "";
                return;
            }

            await this.getRoom(0);
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
        enterRoom(roomId, roomName, isLock) {
            if (!isLock) {
              this.openPopup(roomName, roomId)
            }
        },
        async checkRoomPw() {
            if (this.lockPw === "") return;

            const response = await axios.get(this.serverUrl + "/api/channel/lock",
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

            this.enterState = false;
            this.openPopup(this.roomName, this.roomId)
        },
        handleEnterPop() {
            this.enterState = false;
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
            const response = await axios.get(this.serverUrl + "/api/channel/name/" + this.roomName);
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
          const [navigationEntry] = performance.getEntriesByType("navigation");

          if (navigationEntry.type !== "reload" && this.$route.name !== "Chat") {
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
        },
        handleRoomInfoClick(item) {
            if (this.isMobile) {
                this.handleEnter(item);
            }
        },
        handleRoomInfoDblClick(item) {
            if (!this.isMobile) {
                this.handleEnter(item);
            }
        },
        handleEnter(item) {
            this.handleEnterPop();
            this.getRoomInfo(item.id, item.channelName);

            if (item.isLock) {
              this.enterState = true;
            } else {
              this.enterRoom(item.id, item.channelName, item.isLock);
            }
        },
        openPopup(roomName, roomId) {
          const url = `${window.location.origin}/chat/${roomName}/${roomId}`;
          const windowName = this.roomName;
          const specs = "width=400,height=660,toolbar=no,location=no";
          window.open(url, windowName, specs);
        },
        handleMessage(event) {
          if (event.origin !== this.clientUrl) return;
          const message = event.data;

          if (message.type === "updateChannelCount" && message.channelId) {
            this.updateMemberCount(message.channelId, +1); // 입장 시
          } else if (message.type === "leaveChannelCount" && message.channelId) {
            this.updateMemberCount(message.channelId, -1); // 퇴장 시
          }
        },
        updateMemberCount(channelId, change) {
          const room = this.roomList.find((room) => room.id === channelId);

          if (room) {
            room.memberCount += change;

            if (room.memberCount < 0) {
              room.memberCount = 0;
            }
          }
        },
    },
    computed: {
        visiblePages() {
            if (this.pageSize === 0) {
                return [];
            }
            const start = Math.floor(this.pageNum / this.maxPage) * this.maxPage + 1;
            const end = Math.min(start + this.maxPage - 1, this.pageSize);
            return Array.from({ length: end - start + 1 }, (_, i) => start + i);
        },
    },
    components: {
    },
    mounted() {
        window.addEventListener('beforeunload', this.handleBeforeUnload);
    },
    beforeUnmount() {
        window.removeEventListener('beforeunload', this.handleBeforeUnload);
        window.removeEventListener('message',this.handleMessage);
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

.room-list {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 0.5em;
}

.room-list:hover {
    background-color: rgba(226, 226, 226, 0.5);
}

.sender-button {
    width: 40px;
    height: 30px;
    padding: 0;
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
    justify-content: flex-end;
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

.room-title {
    width:600px;
}

.previous-button {
    width: 200px;
}

.next-button {
    width: 200px;
}

.new-chatting {
  padding: 0px;
  font-size: 15px;
  font-weight: bold;
  color: red;
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

    .room-title {
        width:600px;
        max-width: 45%;
    }

    .previous-button {
        width: 50px;
    }

    .next-button {
        width: 50px;
    }
}
</style>
