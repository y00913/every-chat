<template>
    <footer>
        <p>
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
footer {
  font-weight: bold; 
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 4vh;
}

@media (max-width: 767px) {
  footer {
    margin: 0px;
  }
}

</style>