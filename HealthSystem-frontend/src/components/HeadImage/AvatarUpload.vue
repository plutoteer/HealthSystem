<template>
  <div>
    <input type="file" @change="handleFileChange">
    <img v-if="imageUrl" :src="imageUrl">
  </div>
</template>

<script>
export default {
  name: 'AvatarUpload',
  data() {
    return {
      imageUrl: null
    }
  },
  watch: {
    imageUrl(newValue) {
      this.$emit('input', newValue)
    }
  },
  methods: {
    handleFileChange(event) {
      const file = event.target.files[0]
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.imageUrl = reader.result
      }
    }
  }
}
</script>

  <style>
  /* 样式代码 */
  </style>
