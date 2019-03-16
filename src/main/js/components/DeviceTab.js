const React = require('react');

import { Container, Header, Form, Button, Dropdown, Label, List } from 'semantic-ui-react'

import axios from "axios";
import { findIndex } from 'lodash';

class DeviceTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = {devices: []};
	}

	componentDidMount() {
	    axios.get('/device')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.devices = response.data;
                this.setState(newState);
            });
	}
	handleChange = name => data => {
    	if (data) {
    	    let newState = Object.assign({}, this.state);
    	    newState.selectedItem[name] = data.target.value;
        	this.setState( newState );
     	}
    }

	onDropDownChange = (e, data) => {
		let idx = findIndex(this.state.mqttlist, {publisherId: data.value});
        this.setState({ selectedItem: this.state.mqttlist[idx] });
    }

    onUpdate = (e, data) => {
        const payload = {
            mqttConfig: this.state.selectedItem
        };

        axios.post('/api/mqtt/config', JSON.stringify(payload),{
            headers: {
                'Content-Type': 'application/json',
            }
        }).then(response => {
            let newState = Object.assign({}, this.state);
            newState.mqttlist = response.data;
            this.setState(newState);
        });
    }

    onRemove = (e, data) => {

        let { selectedItem } = this.state
        if (selectedItem && selectedItem.id) {
    	        axios.delete('/api/mqtt/config/' + selectedItem.id, ).then(response => {
	            let newState = Object.assign({}, this.state);
	            newState.mqttlist = response.data;
	            this.setState(newState);
	        });
	        this.setState({ selectedItem: {} })
	     }
    }

	render() {
	    let { devices } = this.state;
        let stateOptions = [];
        if (devices) {
            devices.map(device => {
                stateOptions.push(
                        { key: device.id, value: device.name, text: device.bane }
                );
            })
        }

        return (
            <Container text>
                <List divided selection>
                     <List.Item>
                        <Label horizontal>
                            MQTT brockers:
                        </Label>
                        <Dropdown selection options={stateOptions} onChange={this.onDropDownChange} placeholder='Select device...'/>
                        <Button primary>Add</Button>
                    </List.Item>
                </List>
                <Header as='h2'>Header</Header>

            </Container>
        )

	}
}

export default (DeviceTab);