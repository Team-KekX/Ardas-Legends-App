const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('update')
        .setDescription('Updates information about an entity')
        .addSubcommand(subcommand =>
            subcommand
                .setName('faction')
                .setDescription('Update your faction')
                .addStringOption(option =>
                    option.setName('faction-name')
                        .setDescription('The name of the faction')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('ign')
                .setDescription('Update your minecraft IGN')
                .addStringOption(option =>
                    option.setName('ign')
                        .setDescription('Your new IGN')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('discord-id')
                .setDescription('Update the discord ID of a player')
                .addStringOption(option =>
                    option.setName('old-discord-id')
                        .setDescription('The player\'s old discord ID')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('new-discord-id')
                        .setDescription('The player\'s new discord ID')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        let path = './Bot/commands/subcommands/update/';
        let files = fs.readdirSync(path, (err, tmp_files) => tmp_files.filter(file => file.contains('update_')));
        const commands = {};
        for (const file of files) {
            const name = file.split('update_')[1].slice(0, -3);
            commands[name] = require('./subcommands/update/' + file);
        }
        path = './Bot/commands/subcommands/admin/update/';
        files = fs.readdirSync(path, (err, tmp_files) => tmp_files.filter(file => file.contains('update_')));
        for (const file of files) {
            const name = file.split('update_')[1].slice(0, -3);
            commands[name] = require('./subcommands/admin/update/' + file);
        }
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};
