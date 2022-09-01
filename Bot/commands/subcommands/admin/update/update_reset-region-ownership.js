const {isMemberStaff, capitalizeFirstLetters} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/region/reset-ownership')
            .then(async function (response) {
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Reset all regions to hasChanged false!`)
                    .setColor('GREEN')
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function (error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while updating regions has-changed-ownership")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })

    },
};
