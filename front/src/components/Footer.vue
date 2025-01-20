<template>
    <footer>
        <p class="footer">
          <!-- made by <a v-bind:href="`https://github.com/y00913`" target="_blank" style="text-decoration: none;">y00913</a> -->
          made by <a style="color: #551A8B;">y00913</a>
        </p>
    </footer>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
.footer {
  font-weight: bold; 
  margin-top: 7vh;
}

@media (max-width: 767px) {
  .footer {
    margin-top: 1.5vh;
  }
}
</style>