<template>
    <div id="app">
        <header>
	   <HeaderBar>
           </HeaderBar>
        </header>
        <div class="main">
            <router-view></router-view>
        </div>
        <footer>
            <FootRoom></FootRoom>
        </footer>
    </div>
</template>

<style>
    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }
    /*fix headroom*/
    .headroom-wrapper {
        height: auto !important;
    }
    .main{
        padding: 10px;
    }
</style>

<script>
    import HeaderBar from '@/containers/common/HeaderBar';
    import FootRoom from '@/components/common/FootRoom';


    export default {
        name: 'app',
        components: {
            HeaderBar,
            FootRoom,
        },
        data () {
            return {
            };
        },
        methods: {
        },
    };
</script>
