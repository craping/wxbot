Keyword = {
    data: {
        keywordTree: [
            {
                title: '词库名',
                expand: true,
                render: (h, { root, node, data }) => {
                    return h('span', {
                        style: {
                            display: 'inline-block',
                            width: '100%'
                        }
                    }, [
                        h('span', [
                            h('Icon', {
                                props: {
                                    type: 'ios-folder-outline'
                                },
                                style: {
                                    marginRight: '8px'
                                }
                            }),
                            h('span', data.title)
                        ]),
                        h('span', {
                            style: {
                                display: 'inline-block',
                                float: 'right',
                                marginRight: '32px'
                            }
                        }, [
                            h('Button', {
                                props: Object.assign({}, Keyword.data.buttonProps, {
                                    icon: 'ios-add',
                                    type: 'primary'
                                }),
                                style: {
                                    width: '64px'
                                },
                                on: {
                                    click: () => { Keyword.data.modal = true; }
                                }
                            })
                        ])
                    ]);
                },
                children: [{
                    key:"关键词1",
                    value:"回复1"
                },{
                    key:"关键词2",
                    value:"回复2"
                }]
            }
        ],
        buttonProps: {
            type: 'default',
            size: 'small',
        },
        modal:false
    },
    methods: {
        renderContent(h, { root, node, data }) {
            return h('span', {
                style: {
                    display: 'inline-block',
                    width: '100%'
                }
            }, [
                h('span', [
                    h('Icon', {
                        props: {
                            type: 'ios-paper-outline'
                        },
                        style: {
                            marginRight: '8px'
                        }
                    }),
                    h('span', data.key+"："+data.value)
                ]),
                h('span', {
                    style: {
                        display: 'inline-block',
                        float: 'right',
                        marginRight: '32px'
                    }
                }, [
                    h('Button', {
                        props: Object.assign({}, Keyword.data.buttonProps, {
                            icon: 'ios-create'
                        }),
                        style: {
                            marginRight: '8px'
                        },
                        on: {
                            click: () => { Keyword.data.modal = true; }
                        }
                    }),
                    h('Button', {
                        props: Object.assign({}, Keyword.data.buttonProps, {
                            icon: 'ios-remove'
                        }),
                        on: {
                            click: () => { this.delKeyword(root, node, data) }
                        }
                    })
                ])
            ]);
        },
        addKeyword(data) {
            const children = data.children || [];
            children.push({
                title: 'appended node',
                expand: true
            });
            this.$set(data, 'children', children);
        },
        delKeyword(root, node, data) {
            const parentKey = root.find(el => el === node).parent;
            const parent = root.find(el => el.nodeKey === parentKey).node;
            const index = parent.children.indexOf(data);
            parent.children.splice(index, 1);
        },
        editKeywordOk(){
            this.$Message.success("操作成功!");
        },
        editKeywordCancel(){

        }
    }
}