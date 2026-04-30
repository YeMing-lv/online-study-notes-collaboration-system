<script>
export default {
    computed: {
        reversedMsg: function() {
            return this.msg.split('').reverse().join('')
        }
    }
}
</script>