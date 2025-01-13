import { createApp } from 'vue'
import App from './App.vue'
import router from './router/router'
import VueDOMPurifyHTML from 'vue-dompurify-html'

const vuePurifyHTMLConfig = {
	ADD_TAGS: ['iframe'],
};

createApp(App)
    .use(router)
    .use(VueDOMPurifyHTML, {default: vuePurifyHTMLConfig})
    .mount('#app')
