<template>
    <Card>
        <div class="logo">
           <span>404 not found</span>
        </div>
        <br>
        <br>
        <div>
            <Button @click="goHome" size="large" style="width: 200px;" type="primary">返回首页</Button>
            <Button @click="backPage" size="large" style="width: 200px;margin-left: 40px;" type="primary">返回上一页</Button>
        </div>
   </Card>
</template>
<style scoped>
    .logo {
        /*width: 180px;*/
        font-size: 18px;
        font-weight: 900;
        color: #3399ff;
        margin: 0 auto;
    }
</style>
<script>
export default {
    name: 'Error404',
    methods: {
        backPage () {
            this.$router.go(-1);
        },
        goHome () {
            this.$router.replace({name: 'index'});
        }
    }
};
</script>
