const React = require('react');
const ReactDOM = require('react-dom');
import { Container, Header, Form, Button, Modal, Dropdown, Label, List } from 'semantic-ui-react'

import axios from "axios";

class MQTTTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = {mqttlist: [], question: false, selectedItem: {}};
	}
	
	componentDidMount() {
	    axios.get('/api/mqtt/config')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.config = response.data;
                this.setState(newState);
            });
	}


	render() {
	    let { mqttlist } = this.state;
	    console.log(mqttlist)

        let stateOptions = [];
        if (mqttlist) {
            mqttlist.map(node => {
                stateOptions.push(
                        { key: mode.publisherId, value: mode.publisherId, text: mode.publisherId }
                );
            })
        }

        return (
            <Container text>
                <List divided selection>
                     <List.Item>
                        <Label horizontal>
                            MQTT servers:
                        </Label>
                        <Dropdown selection options={stateOptions}/>
                        <Button primary>Add</Button>
                    </List.Item>
                </List>
                <Header as='h2'>Header</Header>
                <Form>
                   <Form.Field>
                      <label>Publisher Id</label>
                      <input placeholder='publisherId' />
                   </Form.Field>
                   <Form.Field>
                      <label>Host</label>
                      <input placeholder='Host' />
                   </Form.Field>
                   <Form.Field>
                      <label>Port</label>
                      <input placeholder='Port' />
                   </Form.Field>
                   <Button type='submit'>Save</Button>
                </Form>
            </Container>
        )

	}
}

export default (MQTTTab);