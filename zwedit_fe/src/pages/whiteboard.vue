<template>
    <Row type="flex" :gutter="16">
        <Col span="20">
            <Card>
                <codemirror ref="myCm" 
                            v-model="code"
                            :options="cmOptions"
                            @changes="OnCodemirrorChange">
                </codemirror>
            </Card>
        </Col>
        <Col span="4">
            <Card>
	            <div style="height: 800px;">
                    <Alert v-show="form.state" type="success" show-icon>
	                    normal
                    </Alert>
                    <Alert v-show="!form.state" type="error" show-icon>
	                    offline
                    </Alert>
                    <Card>
                        <Form>
	                        <FormItem>
                                <Button type="primary" size="large" long>Language</Button>
                            </FormItem>
	                        <FormItem>            
                                <Select v-model="cmOptions.mode"  placeholder="select">
                                    <Option v-for="i in form.languageList" :value="i.value" :key="i.value">{{i.label}}</Option>
                                </Select>
                            </FormItem>
	                    </Form>
                    </Card>
                    <br>
                    <Card>
                        <Form>
                            <FormItem>
                                <Button type="primary" size="large" long>Theme</Button>
                            </FormItem>
                            <FormItem>
                                <Select v-model="cmOptions.theme" placeholder="select">
                                    <Option v-for="i in form.themeList" :value="i.value" :key="i.value">{{ i.label }}</Option>
                                </Select>
                            </FormItem>
                        </Form>
                    </Card>		
                </div>
            </Card>
        </Col>
    </Row>
</template>
<style>
    .CodeMirror {
        border: 1px solid #eee;
        height: 800px;
        text-align: left;
        height: 800px;
        font-size: 1.3em;
        font-weight: 400;
        border-left: 2px;
    }
</style>
<script>
    // base require
    import {codemirror} from 'vue-codemirror'
    import 'codemirror/lib/codemirror.css'
 
    //language
    import 'codemirror/mode/go/go.js'
    import 'codemirror/mode/javascript/javascript.js'
    import 'codemirror/mode/python/python.js'
    import 'codemirror/mode/clike/clike.js'
  
    //theme css
    import 'codemirror/theme/material.css'
    import 'codemirror/theme/monokai.css'
    import 'codemirror/theme/mbo.css'
    import 'codemirror/theme/solarized.css'
  
    //active line
    import 'codemirror/addon/selection/active-line.js'
  
    // styleSelectedText
    import 'codemirror/addon/selection/mark-selection.js'
    import 'codemirror/addon/search/searchcursor.js' 
  
    // hint
    import 'codemirror/addon/hint/show-hint.js'
    import 'codemirror/addon/hint/show-hint.css'
    import 'codemirror/addon/hint/javascript-hint.js'
    import 'codemirror/addon/selection/active-line.js'
  
    // highlightSelectionMatches
    import 'codemirror/addon/scroll/annotatescrollbar.js'
    import 'codemirror/addon/search/matchesonscrollbar.js'
    import 'codemirror/addon/search/searchcursor.js'
    import 'codemirror/addon/search/match-highlighter.js'
  
    // keyMap
    import 'codemirror/addon/edit/matchtags.js'
    import 'codemirror/addon/edit/matchbrackets.js'
    import 'codemirror/addon/comment/comment.js'
    import 'codemirror/addon/dialog/dialog.js'
    import 'codemirror/addon/dialog/dialog.css'
    import 'codemirror/addon/search/searchcursor.js'
    import 'codemirror/addon/search/search.js'
    import 'codemirror/keymap/sublime.js'
  
    // foldGutter
    import 'codemirror/addon/fold/foldgutter.css'
    import 'codemirror/addon/fold/brace-fold.js'
    import 'codemirror/addon/fold/comment-fold.js'
    import 'codemirror/addon/fold/foldcode.js'
    import 'codemirror/addon/fold/foldgutter.js'
    import 'codemirror/addon/fold/indent-fold.js'
    import 'codemirror/addon/fold/markdown-fold.js'
    import 'codemirror/addon/fold/xml-fold.js'
  
    import request from '@/libs/request';
    import TextOperation from '@/libs/ot/textOperation';
    import Syschronized from '@/libs/ot/syschronized';
    import CodemirrorAdapter from '@/libs/ot/codemirrorAdapter';
    import Client from '@/libs/ot/client';  
    export default {
        components: {
            codemirror
        },
        data() {
            return {
                socket: '',
                code: '',
                roomUrl: '',
                sid: '',
                intervalId: '',
                cmOptions: {
                    tabSize: 4,
                    styleActiveLine: true,
                    mode: 'text/x-csrc',
                    lineNumbers: true,
                    line: true,
                    foldGutter: true,
                    gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
                    highlightSelectionMatches: { showToken: /\w/, annotateScrollbar: true },
                    hintOptions:{
                        // 当匹配只有一项的时候是否自动补全
                        completeSingle: false
                    },
                    showCursorWhenSelecting: true,
                    keyMap: 'sublime',
                    matchBrackets: true,
                    theme: 'default',
                    extraKeys: { 'Ctrl': 'autocomplete' }
                },
                form: {
                    state: true,
                    languageList: [
                        {
                            value: 'text/x-csrc',
                            label: 'c',
                        },
                        { 
                            value: 'text/x-c++src',
                            label: 'c++',
                        },
                        {
                            value: 'text/x-csharp',
                            label: 'c#',
                        },
                        {
                            value: 'text/x-java',
                            label: 'java',
                        },
                        {
                            value: 'text/javascript',
                            label: 'javascript',
                        },
                        {
                            value: 'text/x-python',
                            label: 'python',
                        },	
                        {
                            value: 'text/x-go',
                            label: 'golang',
                        },
                    ],
                    themeList: [
                        {
                            value: 'default',
                            label: 'default',
                        },
                        {
                            value: 'material',
                            label: 'material',
                        },
                        {
                            value: 'monokai',
                            label: 'monakai',
                        },
                        {
                            value: 'mbo',
                            label: 'mbo',
                        },
                        {
                            value: 'solarized light',
                            label: 'solarized light',
                        },	    
                    ],		
                },
                ignoreNextChange : false,    
                client: '',
            }
        },
        methods: {
            createWebSocket(roomUrl, sid) {
                let url = 'ws://' + document.domain + ':8080/\events?' + 'roomUrl=' + this.roomUrl + '&sid=' + this.sid;
                console.log(url);
                this.socket = new WebSocket(url);
                this.socket.onopen = this.wsOpen;
                this.socket.onmessage = this.wsMessage;
	            this.socket.onclose = this.wsClose;
            },
            wsOpen() {
                console.log('Connection open ...');
            },
            wsClose() {
                console.log('Connection closed.');
	        },
            wsMessage(evt) {
                console.log(this.sid);
                let msg = JSON.parse(evt.data);
                console.log(msg);
                if (msg.type === 'new') {
                    this.client = new Client(this.socket, this.codemirror, msg.revision);
                    if(msg.text !== '') {
                        this.ignoreNextChange = true;
                        CodemirrorAdapter.setValue(this.codemirror, msg.text);
                    }    
                } else {
                    if(msg.sid === this.sid) {
                        this.client.serverAck();
                    } else {
                        if(msg.actions.length !== 0) {
                            this.ignoreNextChange = true;
                            this.client.applyServer(new TextOperation(msg.actions));
                        }
                    }
                }  
            },
            OnCodemirrorChange(cm, arr) {
                if(!this.ignoreNextChange) {
                    var curOp = CodemirrorAdapter.operationFromCodeMirrorChanges(arr, this.codemirror);
                    console.log('changes');
                    console.log(curOp);
                    this.client.applyClient(curOp);
                }
                this.ignoreNextChange = false;
            }
        },
        mounted() {
            this.roomUrl = this.$route.params.id;
            request({
                url: '/whiteboards/' + this.roomUrl,
                method: 'get',
            }).then((res) => {
                console.log(res);
                if(!res.msg.exist) {
                    this.$router.replace({path: '/404'});
                    return;
                }
                this.sid = res.msg.sid;
                this.createWebSocket(this.roomUrl, this.sid);
                let that = this;
                this.intervalId = setInterval(function(){
                    if(that.socket.readyState !== WebSocket.OPEN) {
                        that.form.state = false;
                    }
                }, 1000);
            });
        },
        beforeDestroy () {
            console.log(this.socket);
            if(this.socket !== '') {
                this.socket.close();
            }
            if(this.intervalId !== '') {
                clearInterval(this.intervalId);
            }
        },
        computed: {
            codemirror() {
                return this.$refs.myCm.codemirror
            }
        },
    }
</script>
