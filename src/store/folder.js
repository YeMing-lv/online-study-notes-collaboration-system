import { defineStore } from "pinia";

export const useFolderStore = defineStore('folderStore', {
    state: () => ({
        currentFolder: {},
    }),
    actions: {
        init: {
            
        }

    },
})