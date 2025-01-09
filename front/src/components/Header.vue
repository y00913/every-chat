<template>
    <header style="margin-top:5vh; margin-bottom: 1vh;">
        <title>every chat</title>
        <link rel="shortcut icon" type="image/x-icon" href="https://cdn-icons-png.flaticon.com/512/5962/5962500.png">
        <h1>{{ title }}</h1>
        <input type="checkbox" @click="handleTheme">
    </header>
</template>

<script>
export default {
    name: 'App',
    data() {
        return {
            title: '에브리 챗 :)',
        }
    },
    methods: {
        handleTheme() {
            document.documentElement.style.filter = "invert(100%) hue-rotate(180deg)";
            document.documentElement.style.webkitFilter = "invert(100%) hue-rotate(180deg)";
        }
    }
}
</script>

<style></style>
